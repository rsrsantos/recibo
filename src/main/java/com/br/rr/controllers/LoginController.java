package com.br.rr.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.br.rr.config.PlanoSuccessHandler;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String login(@RequestParam(required = false) Long plano, HttpSession session) {
		if (plano != null) {
			session.setAttribute(PlanoSuccessHandler.SESSION_PLANO, plano);
		}
		return "login";
	}

}
