package com.example.nuggetbe.repository;

import com.example.nuggetbe.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);
    Member findByUuid(UUID uuid);

}

