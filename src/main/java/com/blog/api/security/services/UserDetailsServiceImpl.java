package com.blog.api.security.services;

import com.blog.api.models.Accounts;
import com.blog.api.repositories.AccountsRepository;
import com.blog.api.security.principals.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AccountsRepository accountsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Accounts account = accountsRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user with username: " + username));

        return new UserPrincipal(account);
    }
}
