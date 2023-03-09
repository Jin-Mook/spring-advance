package com.kt.myrestapi.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.myrestapi.accounts.Account;
import com.kt.myrestapi.accounts.AccountAdapter;
import com.kt.myrestapi.accounts.AccountService;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final AccountService accountService;
    private final Environment env;

    private final String TOKEN_PREFIX = "Bearer";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("CustomAuthorizationFilter start...");
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getMethod().equals("PUT") && requestURI.contains("/api/lectures")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            String token = authorizationHeader.substring(TOKEN_PREFIX.length());

            String tokenSubject = getJwtSubject(token);
            if (tokenSubject == null) {
                error401Print(response, "JWT Token is not valid");
            } else {
                Account account = accountService.getAccountByAccountId(tokenSubject);
                AccountAdapter accountAdapter = new AccountAdapter(account);
                Authentication authentication = new UsernamePasswordAuthenticationToken(accountAdapter, null, accountAdapter.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }
        } else {
            error401Print(response, "JWT 토큰이 필요합니다.");
        }
    }

    private void error401Print(HttpServletResponse response, String errMsg) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = new HashMap<>();
        body.put("code", HttpStatus.UNAUTHORIZED.value());
        body.put("error", errMsg);

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }

    private String getJwtSubject(String jwt) {
        log.info("jwt = {}", jwt);

        String subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                .parseClaimsJws(jwt).getBody()
                .getSubject();
        log.info("subject = {}", subject);

        return subject;
    }
}
























