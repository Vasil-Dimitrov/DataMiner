package com.dataminer.controller;

import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Vasil.Dimitrov^2
 *
 */
public abstract class BaseController {
	public ModelAndView view(String viewName) {
		return this.view(viewName, new ModelAndView());
	}

	public ModelAndView view(String viewName, ModelAndView model) {
		model.setViewName("views/" + viewName);
		return model;
	}

	public ModelAndView redirect(String redirectUrl) {
		ModelAndView model = new ModelAndView();
		model.setViewName("redirect:" + redirectUrl);
		return model;
	}
}
