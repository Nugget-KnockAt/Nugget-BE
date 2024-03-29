package com.example.nuggetbe.repository;

import com.example.nuggetbe.entity.Member;
import com.example.nuggetbe.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {


    Optional<Object> findByMemberAndAction(Member member, String action);

    List<Message> findByMember(Member member);
}
