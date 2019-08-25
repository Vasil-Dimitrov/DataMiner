package com.dataminer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Vasil.Dimitrov^2
 *
 */
@Controller
public class AboutController extends BaseController {

	@GetMapping("/about")
	public ModelAndView showAboutPage(ModelAndView modelAndView) {
		return view("about");
	}

}
