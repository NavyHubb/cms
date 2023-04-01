package com.zerobase.cms.user.service.customer;

import com.zerobase.cms.user.domain.customer.ChangeBalanceForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.model.CustomerBalanceHistory;
import com.zerobase.cms.user.domain.repository.CustomerBalanceHistoryRepository;
import com.zerobase.cms.user.domain.repository.CustomerRepository;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerBalanceHistoryServiceTest {

    @Mock
    private CustomerBalanceHistoryRepository customerBalanceHistoryRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerBalanceHistoryService customerBalanceHistoryService;

    @Test
    @DisplayName("잔액변경_성공")
    void changeBalance_success() {
        // given
        Customer customer = Customer.builder()
                                    .id(123L)
                                    .email("test@naver.com")
                                    .name("danny")
                                    .password("password11!!")
                                    .birth(LocalDate.now())
                                    .phone("010-0000-1111")
                                    .build();

        ChangeBalanceForm form = ChangeBalanceForm.builder()
                .from("USER")
                .message("CHARGE")
                .money(50000)
                .build();

        given(customerRepository.findById(anyLong()))
                .willReturn(Optional.of(customer));

        given(customerBalanceHistoryRepository.findFirstByCustomer_IdOrderByIdDesc(customer.getId()))
                .willReturn(Optional.empty());

        CustomerBalanceHistory customerBalanceHistory = CustomerBalanceHistory.builder()
                .changeMoney(0)
                .currentMoney(0)
                .customer(customer)
                .build();

        given(customerBalanceHistoryRepository.save(any()))
                .willReturn(
                        CustomerBalanceHistory.builder()
                        .customer(customerBalanceHistory.getCustomer())
                        .changeMoney(form.getMoney())
                        .currentMoney(customerBalanceHistory.getCurrentMoney() + form.getMoney())
                        .fromMessage(form.getFrom())
                        .description(form.getMessage())
                        .build());

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        // when
        // 여기 들어가는 파라미터는 의미가 없음. 뭘 넣든 given 절에서 설정한대로
        CustomerBalanceHistory result =
                customerBalanceHistoryService.changeBalance(customer.getId(), form);

        // then
        verify(customerBalanceHistoryRepository, times(1)).save(any());
        verify(customerRepository, times(1)).findById(captor.capture());
        assertEquals(123, captor.getValue());
        assertEquals(50000, result.getChangeMoney());
    }

    @Test
    @DisplayName("잔액변경_실패_잔액부족")
    void changeBalance_notEnoughBalance() {
        // given
        Customer customer = Customer.builder()
                .id(123L)
                .email("test@naver.com")
                .name("danny")
                .password("password11!!")
                .birth(LocalDate.now())
                .phone("010-0000-1111")
                .build();

        ChangeBalanceForm form = ChangeBalanceForm.builder()
                .from("USER")
                .message("ORDER")
                .money(-50000)  // 잔액 차감
                .build();

        given(customerRepository.findById(anyLong()))
                .willReturn(Optional.of(customer));

        given(customerBalanceHistoryRepository.findFirstByCustomer_IdOrderByIdDesc(customer.getId()))
                .willReturn(Optional.empty());

        CustomerBalanceHistory customerBalanceHistory = CustomerBalanceHistory.builder()
                .changeMoney(0)
                .currentMoney(0)  // 현재 잔액이 없는 상황
                .customer(customer)
                .build();

        // when
        CustomException exception = assertThrows(CustomException.class,
                () -> customerBalanceHistoryService.changeBalance(customer.getId(), form));

        // then
        assertEquals(ErrorCode.NOT_ENOUGH_BALANCE, exception.getErrorCode());
    }
}