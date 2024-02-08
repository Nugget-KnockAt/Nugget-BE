package com.example.nuggetbe.repository;

import com.example.nuggetbe.entity.Event;
import com.example.nuggetbe.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByMemberAndCreatedAtAfter(Member member, LocalDateTime time);
}
