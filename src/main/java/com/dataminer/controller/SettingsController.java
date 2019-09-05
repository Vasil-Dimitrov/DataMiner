package com.dataminer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dataminer.constant.View;
import com.dataminer.entity.AlgoSettings;

/**
 * Controller for working with the /settings context
 *
 * @author Vasil.Dimitrov^2
 *
 */
@Controller
public class SettingsController extends BaseController {

	@GetMapping(View.SETTINGS_URL)
	public ModelAndView showSettingsPage(ModelAndView modelAndView) {
		AlgoSettings mockSettings = new AlgoSettings(true, 0.4, true, 0.2, 0.3, true);
		modelAndView.addObject("algo_settings", mockSettings);
		return view(View.SETTINGS_VIEW, modelAndView);
	}

	@PostMapping(View.SETTINGS_URL)
	public ModelAndView updateSettingsPage(ModelAndView modelAndView) {
		return view(View.SETTINGS_VIEW);
	}
}
