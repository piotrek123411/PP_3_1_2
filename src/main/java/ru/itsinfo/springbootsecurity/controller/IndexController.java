package ru.itsinfo.springbootsecurity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.itsinfo.springbootsecurity.service.AppService;

@Controller
@RequestMapping("/")
public class IndexController {
	private final AppService appService;

	@Autowired
	public IndexController(AppService appService) {
		this.appService = appService;
	}

	@GetMapping("")
	public String welcomePage() {
		return "index";
	}

	@GetMapping("/403")
	public String accessDeniedPage() {
		return "access-denied";
	}
}