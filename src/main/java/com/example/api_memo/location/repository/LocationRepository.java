package com.example.api_memo.location.repository;

import com.example.api_memo.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByNxAndNy(Integer nx, Integer ny);
}
