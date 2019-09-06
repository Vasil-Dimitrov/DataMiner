package com.dataminer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dataminer.constant.View;
import com.dataminer.pojo.entity.AlgoSettings;
import com.dataminer.service.AlgoSettingsService;

/**
 * Controller for working with the /settings context
 *
 * @author Vasil.Dimitrov^2
 *
 */
@Controller
public class SettingsController extends BaseController {

	private final AlgoSettingsService service;

	@Autowired
	public SettingsController(AlgoSettingsService service) {
		this.service = service;
	}

	@GetMapping(View.SETTINGS_URL)
	public ModelAndView showSettingsPage(ModelAndView modelAndView) {
		modelAndView.addObject("algoSettings", this.service.getDefaultAlgoSettings());
		return view(View.SETTINGS_VIEW, modelAndView);
	}

	@PostMapping(View.SETTINGS_URL)
	public ModelAndView updateSettingsPage(@ModelAttribute(value = "algoSettings") AlgoSettings algoSettings, ModelAndView modelAndView) {
		if (this.service.saveAlgoSettings(algoSettings)) {
			modelAndView.addObject("success_msg", "Промените са успешно запазени!");
		} else {
			modelAndView.addObject("error_msg", "Възникна грешка!");
		}
		System.out.println(algoSettings.toString());
		return view(View.SETTINGS_VIEW, modelAndView);
	}
}
