package com.springframework.burgerwebapp.controllers;

import com.springframework.burgerwebapp.repositories.VenueRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class VenueController {

    private final VenueRepository venueRepository;

    public VenueController(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    //Root URL - Venues
    @RequestMapping("/")
    public String getVenues1(Model model) {
        model.addAttribute("venues", venueRepository.findAll());

        return "venues/list";
    }
}
