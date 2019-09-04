package com.dataminer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dataminer.entity.AlgoSettings;

/**
 *
 * @author Vasil.Dimitrov^2
 *
 */
@Controller
public class SettingsController extends BaseController {

	@GetMapping("/settings")
	public ModelAndView showSettingsPage(ModelAndView modelAndView) {
		AlgoSettings mockSettings = new AlgoSettings(true, 0.4, true, 0.2, 0.3, true);
		modelAndView.addObject("algo_settings", mockSettings);
		return view("settings", modelAndView);
	}

	@PostMapping("/settings")
	public ModelAndView updateSettingsPage(ModelAndView modelAndView) {
		return view("settings");
	}
}
