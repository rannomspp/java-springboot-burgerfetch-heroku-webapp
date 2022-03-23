package com.springframework.burgerwebapp.repositories;

import com.springframework.burgerwebapp.domain.Burger;
import org.springframework.data.repository.CrudRepository;

public interface BurgerRepository extends CrudRepository<Burger, Long> {
}
