package com.example.nuggetbe.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.nuggetbe.entity.Member;
import com.example.nuggetbe.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomMemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username);
        if (member == null){
            throw new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다.");
        }
        UserDetails userdetails = createUser(username, member);
        return userdetails;
    }


    private User createUser(String username, Member member) {
        List<GrantedAuthority> grantedAuthorities = List.of(new SimpleGrantedAuthority(member.getRole().toString()));
        return new User(member.getEmail(),
                member.getPassword(),
                grantedAuthorities);
    }
}
