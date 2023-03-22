package com.zerobase.cms.user.application;

import com.zerobase.cms.user.client.MailgunClient;
import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.exception.ErrorCode;
import com.zerobase.cms.user.mailgun.SendMailForm;
import com.zerobase.cms.user.service.SignupCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpApplication {
    private final MailgunClient mailgunClient;
    private final SignupCustomerService signupCustomerService;

    public void customerVerify(String email, String code) {
        signupCustomerService.verifyEmail(email, code);
    }

    // 회원가입
    public String customerSignUp(SignUpForm form) {
        if (signupCustomerService.isEmailExist(form.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_USER);
        } else {
            Customer c = signupCustomerService.signUp(form);

            String verificationCode = getRandomCode();
            SendMailForm sendMailForm = SendMailForm.builder()
                    .from("tester@gmail.com")
                    .to(c.getEmail())
                    .subject("Verification Email")
                    .text(getVerificationEmailBody(c.getEmail(), c.getName(), verificationCode))
                    .build();

            // 사용자에게 인증메일 발송
            log.info("Send email result : " + mailgunClient.sendEmail(sendMailForm).getBody());

            // 인증코드를 사용자 계정에 등록
            String expireDateTime = signupCustomerService.changeCustomerValidateEmail(c.getId(), verificationCode).toString();

            return "회원가입에 성공하였습니다. 인증 기한은 "+ expireDateTime + "입니다.";
        }
    }

    // 랜덤 인증코드 생성
    private String getRandomCode() {
        return RandomStringUtils.random(10, true, true);
    }

    private String getVerificationEmailBody(String email, String name, String code) {
        StringBuilder sb = new StringBuilder();
        return sb.append("Hello ").append(name).append("! Please Click Link for Verification.\n\n")
                .append("http://localhost8080/customer/signup/verify?email=")
                .append(email)
                .append("&code=")
                .append(code).toString();
    }

}
