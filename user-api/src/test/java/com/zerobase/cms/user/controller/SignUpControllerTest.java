package com.zerobase.cms.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.cms.user.application.SignUpApplication;
import com.zerobase.cms.user.domain.SignUpForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SignUpController.class)  // 웹에서 사용되는 요청과 응답에 대한 테스트를 수행할 수 있음
class SignUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    SignUpApplication signUpApplication;


    @Test
    @DisplayName("고객-회원가입-성공")
    void customerSignUp_success() throws Exception {
        SignUpForm form = SignUpForm.builder()
                .email("test@naver.com")
                .name("danny")
                .password("password11!!")
                .birth(LocalDate.now())
                .phone("010-0000-1111")
                .build();

        mockMvc.perform(
                post("/signUp/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("고객-회원가입-실패")
    void customerSignUp_fail() throws Exception {
        SignUpForm form = SignUpForm.builder()
                .email("testnaver.com")  // 유효성 검사 실패할 부분!
                .name("danny")
                .password("password11!!")
                .birth(LocalDate.now())
                .phone("010-0000-1111")
                .build();

        mockMvc.perform(
                        post("/signUp/customer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(form)))
                .andExpect(jsonPath("$.errorCode").value("INVALID_VALUE"))
                .andDo(print());
    }

    @Test
    @DisplayName("판매자-회원가입-성공")
    void sellerSignUp_success() throws Exception {
        SignUpForm form = SignUpForm.builder()
                .email("test@naver.com")
                .name("danny")
                .password("password11!!")
                .birth(LocalDate.now())
                .phone("010-0000-1111")
                .build();

        mockMvc.perform(
                        post("/signUp/seller")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("판매자-회원가입-실패")
    void sellerSignUp_fail() throws Exception {
        SignUpForm form = SignUpForm.builder()
                .email("test@naver.com")
                .name("danny")
                .password("password11!!")
                .birth(LocalDate.now())
                .phone("010-0000-")  // 유효성 검사 실패할 부분!
                .build();

        mockMvc.perform(
                        post("/signUp/seller")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(form)))
                .andExpect(jsonPath("$.errorCode").value("INVALID_VALUE"))
                .andDo(print());
    }

}