package com.example.pract4;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends ReactiveCrudRepository<Hotel, Long> {
}

