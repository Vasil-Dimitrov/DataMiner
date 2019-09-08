package com.dataminer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dataminer.constant.RequestAttribute;
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

	/**
	 * Method returning the settings view
	 * 
	 * @param modelAndView
	 * @return
	 */
	@GetMapping(View.SETTINGS_URL)
	public ModelAndView showSettingsPage(ModelAndView modelAndView) {
		modelAndView.addObject(RequestAttribute.ALGO_SETTINGS, this.service.getDefaultAlgoSettings());
		return view(View.SETTINGS_VIEW, modelAndView);
	}

	/**
	 * Method for saving the new settings page. Before saving, the values are checked if they
	 * are valid and an appropriate error message is returned for the user to see.
	 * 
	 * @param algoSettings
	 * @param modelAndView
	 * @return
	 */
	@PostMapping(View.SETTINGS_URL)
	public ModelAndView updateSettingsPage(@ModelAttribute(value = RequestAttribute.ALGO_SETTINGS) AlgoSettings algoSettings,
			ModelAndView modelAndView) {
		List<String> errors = algoSettings.isValid();
		if (!CollectionUtils.isEmpty(errors)) {
			modelAndView.addObject(RequestAttribute.ERROR_MSG, errors);
			return view(View.SETTINGS_VIEW, modelAndView);
		}

		if (this.service.saveAlgoSettings(algoSettings)) {
			modelAndView.addObject(RequestAttribute.SUCCESS_MSG, "Промените са успешно запазени!");
		} else {
			modelAndView.addObject(RequestAttribute.ERROR_MSG, "Възникна грешка при опит за запазване на промените!");
		}
		System.out.println(algoSettings.toString());
		return view(View.SETTINGS_VIEW, modelAndView);
	}
}
