package com.example.nuggetbe.repository;

import com.example.nuggetbe.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
