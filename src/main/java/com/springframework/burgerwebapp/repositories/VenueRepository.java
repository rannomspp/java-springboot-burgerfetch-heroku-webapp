package com.springframework.burgerwebapp.repositories;

import com.springframework.burgerwebapp.domain.Venue;
import org.springframework.data.repository.CrudRepository;

public interface VenueRepository extends CrudRepository<Venue, Long> {
}
