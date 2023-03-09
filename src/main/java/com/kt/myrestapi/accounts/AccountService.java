package com.kt.myrestapi.accounts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * UserDetailsService 를 구현한 서비스이다.
 * 해당 클래스에서 인증 로직을 수행할 수 있다.??
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService implements UserDetailsService {
    
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("username = {}", username);
//        Account account = accountRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
        Account account = accountRepository.findByEmail(username).get();
        log.info("account id = {}, email = {}", account.getId(), account.getEmail());
        return new AccountAdapter(account);
    }

    private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    public Account saveAccount(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountRepository.save(account);
    }

    public Account getAccountByAccountId(String accountId) {
        Account account = accountRepository.findById(Integer.valueOf(accountId)).orElseThrow(() -> new UsernameNotFoundException(accountId));
        return account;
    }
}
