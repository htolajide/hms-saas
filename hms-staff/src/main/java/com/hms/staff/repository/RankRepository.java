package com.hms.staff.repository;

import com.hms.staff.entity.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankRepository extends JpaRepository<Rank, Long> {
    Optional<Rank> findByName(String name);

    List<Rank> findByHospitalId(Long hospitalId);
}