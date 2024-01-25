package com.example.nuggetbe.service;

import com.example.nuggetbe.entity.Connection;
import com.example.nuggetbe.entity.Member;
import com.example.nuggetbe.repository.ConnectionRepository;
import com.example.nuggetbe.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ConnectionRepository connectionRepository;

    public Member getMemberByUuid(String uuid) {
        return memberRepository.findByUuid(UUID.fromString(uuid));
    }

    @Transactional
    public void createConnection(String uuid, String id) {
        Long authenticatedMemberId = Long.parseLong(id);
        Member member1 = getMemberByUuid(uuid);
        Member member2 = getMemberById(authenticatedMemberId);

        if (member1 != null && member2 != null) {
            Connection connection = new Connection();
            connection.setMember(member1);
            connection.setGuardian(member2);
            connectionRepository.save(connection);
        }
    }

    private Member getMemberById(Long authenticatedMemberId) {
        return memberRepository.findById(authenticatedMemberId).orElse(null);
    }


}
