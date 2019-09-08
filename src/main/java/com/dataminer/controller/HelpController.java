package com.dataminer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dataminer.constant.View;

/**
 * Controller for working with the /help context
 *
 * @author Vasil.Dimitrov^2
 *
 */
@Controller
public class HelpController extends BaseController {

	/**
	 * Method returning the /help view
	 * 
	 * @param modelAndView
	 * @return
	 */
	@GetMapping(View.HELP_URL)
	public ModelAndView showHelpPage(ModelAndView modelAndView) {
		return view(View.HELP_VIEW);
	}
}
