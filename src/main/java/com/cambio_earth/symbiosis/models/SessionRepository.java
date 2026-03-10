package com.cambio_earth.symbiosis.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByIsBreakoutTrue();
}