package com.example.api_memo.location.service;

import com.example.api_memo.location.entity.Location;
import com.example.api_memo.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public String getRegion(Integer nx, Integer ny) {
        List<Location> locations = locationRepository.findByNxAndNy(nx, ny);
        return locations.get(0).toString();
    }
}
