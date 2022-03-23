package com.springframework.burgerwebapp.controllers;

import com.springframework.burgerwebapp.repositories.BurgerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BurgerController {

    private final BurgerRepository burgerRepository;

    public BurgerController(BurgerRepository burgerRepository) {
        this.burgerRepository = burgerRepository;
    }

    @RequestMapping("/burgers")
    public String getBurgers(Model model) {
        model.addAttribute("burgers", burgerRepository.findAll());

        return "burgers/list";
    }

}
