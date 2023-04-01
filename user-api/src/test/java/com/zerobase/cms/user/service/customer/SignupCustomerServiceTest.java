package com.zerobase.cms.user.service.customer;

import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
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
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class SignupCustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private SignUpCustomerService signupCustomerService;

    @Test
    @DisplayName("고객_회원가입_성공")
    void signUp_success() {
        // given
        SignUpForm form = SignUpForm.builder()
                .email("test@naver.com")
                .name("danny")
                .password("password11!!")
                .birth(LocalDate.now())
                .phone("010-0000-1111")
                .build();

        given(customerRepository.save(any()))
                .willReturn(
                        Customer.builder()
                                .id(123L)
                                .email("test@naver.com")
                                .name("danny")
                                .password("password11!!")
                                .birth(LocalDate.now())
                                .phone("010-0000-1111")
                                .build());

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);

        // when
        Customer returnedCustomer = signupCustomerService.signUp(form);

        // then
        verify(customerRepository, times(1)).save(captor.capture());
        assertEquals(123, returnedCustomer.getId());
        assertEquals("010-0000-1111", captor.getValue().getPhone());
    }

    @Test
    @DisplayName("이메일 중복 확인")
    void isEmailExist() {
        //given
        Customer customer = Customer.builder()
                .id(123L)
                .email("test@naver.com")
                .name("danny")
                .password("password11!!")
                .birth(LocalDate.now())
                .phone("010-0000-1111")
                .build();
        String email = customer.getEmail();

        given(customerRepository.findByEmail(customer.getEmail()))
                .willReturn(Optional.of(customer));

        //when
        //then
        assertTrue(signupCustomerService.isEmailExist(email));
    }

    @Test
    @DisplayName("고객_인증코드 정보 입력_실패")
    void changeCustomerValidateEmail_fail() {
        //given
        given(customerRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        CustomException exception = assertThrows(CustomException.class,
                () -> signupCustomerService.changeCustomerValidateEmail(1L, "test@naver.com"));

        //then
        assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());
    }

    @Test
    @DisplayName("고객_인증코드 확인_성공")
    void verifyEmail() {
        //given
        Customer customer = Customer.builder()
                .id(123L)
                .email("test@naver.com")
                .verifyExpiredAt(LocalDateTime.now().plusDays(1))
                .verificationCode("code")
                .verify(false)
                .build();

        given(customerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(customer));

        //when
        signupCustomerService.verifyEmail(customer.getEmail(), "code");

        //then
        assertTrue(customer.isVerify());
    }

    @Test
    @DisplayName("고객_인증코드 확인_실패_이미 인증된 회원")
    void verifyEmail_alreadyVerified() {
        //given
        Customer customer = Customer.builder()
                .id(123L)
                .email("test@naver.com")
                .verifyExpiredAt(LocalDateTime.now().plusDays(1))
                .verificationCode("code")
                .verify(true)  // 이미 인증됨
                .build();

        given(customerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(customer));

        //when
        CustomException exception = assertThrows(CustomException.class,
                () -> signupCustomerService.verifyEmail(customer.getEmail(), "code"));

        //then
        assertEquals(ErrorCode.ALREADY_VERIFIED, exception.getErrorCode());
    }

    @Test
    @DisplayName("고객_인증코드 확인_실패_틀린 인증 코드")
    void verifyEmail_wrongVerification() {
        //given
        Customer customer = Customer.builder()
                .id(123L)
                .email("test@naver.com")
                .verifyExpiredAt(LocalDateTime.now().plusDays(1))
                .verificationCode("code")
                .verify(false)
                .build();

        given(customerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(customer));

        //when
        CustomException exception = assertThrows(CustomException.class,
                () -> signupCustomerService.verifyEmail(customer.getEmail(), "wrong"));

        //then
        assertEquals(ErrorCode.WRONG_VERIFICATION, exception.getErrorCode());
    }

    @Test
    @DisplayName("고객_인증코드 확인_실패_인증기한 경과")
    void verifyEmail_expiredCode() {
        //given
        Customer customer = Customer.builder()
                .id(123L)
                .email("test@naver.com")
                .verifyExpiredAt(LocalDateTime.now().minusDays(1))  // 인증기한 경과
                .verificationCode("code")
                .verify(false)
                .build();

        given(customerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(customer));

        //when
        CustomException exception = assertThrows(CustomException.class,
                () -> signupCustomerService.verifyEmail(customer.getEmail(), "code"));

        //then
        assertEquals(ErrorCode.EXPIRED_CODE, exception.getErrorCode());
    }
}