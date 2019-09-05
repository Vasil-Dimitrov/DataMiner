package com.dataminer.controller;

import java.util.ArrayList;
import java.util.List;

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
		List<String> resources = new ArrayList<>();
		resources.add("https://www.google.com");
		resources.add("https://github.com/AleksandarKovachev");
		resources.add("https://github.com/danielm");
		resources.add("https://github.com/sambaf");
		resources.add("https://github.com/mogumogu2333");
		resources.add("https://github.com/spring-guides");
		resources.add("https://github.com/Java-Techie-jt");
		resources.add("https://www.youtube.com/channel/UC_VaUfeuMVRvFJKPSrYVxGw");
		resources.add("https://www.mkyong.com");
		resources.add("https://stackoverflow.com");
		resources.add("https://www.baeldung.com");
		resources.add("https://dzone.com");
		resources.add("https://docs.oracle.com/en/java/");
		resources.add("https://www.javadevjournal.com/java/java-8-format-localdatetime/");
		resources.add("https://getbootstrap.com/docs/4.0/components/navbar/");
		resources.add("https://www.w3schools.com/css/");
		modelAndView.addObject("resourcesUsed", resources);
		return view(View.RESOURCES_VIEW, modelAndView);
	}

}
