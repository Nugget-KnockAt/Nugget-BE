package com.example.nuggetbe.config;

import com.example.nuggetbe.entity.Member;
import com.example.nuggetbe.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailService implements UserDetailsService {


    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long id = Long.parseLong(username);
        Member member = memberRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new CustomUserDetails(member);
    }
    public class CustomUserDetails implements UserDetails {

        private Member member;

        public CustomUserDetails(Member member) {
            this.member = member;
        }

        @Override
        public String getUsername() {
            return member.getId().toString();
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.emptyList();
        }

        @Override
        public String getPassword() {
            return member.getPassword();
        }

        @Override
        public boolean isAccountNonExpired() {
            // 계정이 만료되지 않았는지
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            // 계정이 잠겨있지 않은지
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            // 자격 증명이 만료되지 않았는지
            return true;
        }

        @Override
        public boolean isEnabled() {
            // 계정이 활성화되어 있는지
            return true;
        }

    }
}


