package com.kt.myrestapi.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.myrestapi.accounts.Account;
import com.kt.myrestapi.accounts.AccountAdapter;
import com.kt.myrestapi.accounts.AccountService;
import com.kt.myrestapi.common.RequestLogin;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AccountService accountService;
    private Environment env;

    private ObjectMapper objectMapper = new ObjectMapper();

    public CustomAuthenticationFilter() {
    }

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, AccountService accountService, Environment env) {
        this.env = env;
        this.accountService = accountService;
        super.setAuthenticationManager(authenticationManager);
    }

    //http://localhost:8080/login 로그인할때 호출됨
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            RequestLogin requestLogin =
                    new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

            log.info("requestLogin = {}", requestLogin);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    requestLogin.getEmail(),
                    requestLogin.getPassword(),
                    new ArrayList<>()
            );

            return getAuthenticationManager().authenticate(authentication);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    //로그인 성공하면 인증토큰 생성하기기
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        //String userName = ((User)authResult.getPrincipal()).getUsername();
        //System.out.println("userName = " + userName);
        AccountAdapter accountAdapter = (AccountAdapter)authResult.getPrincipal();
        Account account = accountAdapter.getAccount();
        System.out.println("accountId " + account.getId());

        String token = Jwts.builder()
                .setSubject(String.valueOf(account.getId()))
                .setExpiration(new Date(System.currentTimeMillis() +
                        Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();
        response.addHeader("token", token);
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        String data = objectMapper.writeValueAsString(map);
        response.getWriter().write(data);
    }
}
