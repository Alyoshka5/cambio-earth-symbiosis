package com.cambio_earth.symbiosis.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreakoutBlockRankingRepository extends JpaRepository<BreakoutBlockRanking, Long> {

}
