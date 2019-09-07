package com.dataminer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dataminer.constant.View;

/**
 * Controller for working with the /about and the /resources context
 *
 * @author Vasil.Dimitrov^2
 *
 */
@Controller
public class AboutController extends BaseController {

	@GetMapping(View.ABOUT_URL)
	public ModelAndView showAboutPage(ModelAndView modelAndView) {
		return view(View.ABOUT_VIEW);
	}

	@GetMapping(View.RESOURCES_URL)
	public ModelAndView showResourcesPage(ModelAndView modelAndView) {
		return view(View.RESOURCES_VIEW, modelAndView);
	}

}
