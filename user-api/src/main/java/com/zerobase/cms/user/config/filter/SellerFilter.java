package com.zerobase.cms.user.config.filter;

import com.zerobase.cms.user.service.customer.CustomerService;
import com.zerobase.cms.user.service.seller.SellerService;
import com.zerobase.domain.common.UserVo;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/seller/*")  // 이 경로로 들어오는 모든 요청에 필터를 건다
@RequiredArgsConstructor
public class SellerFilter implements Filter {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final SellerService sellerService;

    // 요청을 처리하기 전에 필터에서 토큰 인증
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader("X-AUTH-TOKEN");

        // 유효성 검사
        if (!jwtAuthenticationProvider.validateToken(token)) {
            throw new ServletException("Invalid Access");
        }

        // 토큰이 담고 있는 사용자 정보가 존재하는 건지 확인
        UserVo vo = jwtAuthenticationProvider.getUserVo(token);
        sellerService.findByIdAndEmail(vo.getId(), vo.getEmail()).orElseThrow(
                            () -> new ServletException("Invalid Aceess")
        );

        chain.doFilter(request, response);
    }
}
