package com.dataminer.controller;

import org.springframework.web.servlet.ModelAndView;

import com.dataminer.constant.View;

/**
 * This class is meant to save some repeating code across the different controllers
 *
 * @author Vasil.Dimitrov^2
 *
 */
public abstract class BaseController {
	public ModelAndView view(String viewName) {
		return this.view(viewName, new ModelAndView());
	}

	/**
	 * Adds the correct prefix to the viewName, then adds the viewName itself to the model.
	 * Finally returns the {@link ModelAndView}
	 * 
	 * @param viewName
	 * @param model
	 * @return
	 */
	public ModelAndView view(String viewName, ModelAndView model) {
		model.setViewName(View.VIEWS_PREFIX + viewName);
		return model;
	}

	/**
	 * Method meant for redirecting by passing the redirecr URL directly without having to add
	 * the "redirect:" prefix or create a {@link ModelAndView}
	 * 
	 * @param redirectUrl
	 * @return
	 */
	public ModelAndView redirect(String redirectUrl) {
		ModelAndView model = new ModelAndView();
		model.setViewName("redirect:" + redirectUrl);
		return model;
	}
}
