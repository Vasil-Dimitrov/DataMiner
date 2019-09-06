package com.dataminer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.dataminer.algorithm.lcm.AlgoLCM;
import com.dataminer.algorithm.lcm.Dataset;
import com.dataminer.algorithm.pattern.Itemsets;
import com.dataminer.algorithm.rpgrowth.AlgoRPGrowth;
import com.dataminer.constant.Constant;
import com.dataminer.constant.View;
import com.dataminer.pojo.LogFile;
import com.dataminer.pojo.entity.AlgoSettings;
import com.dataminer.service.AlgoSettingsService;
import com.dataminer.util.MockUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Home controller for working with the /index context
 *
 * @author Vasil.Dimitrov^2
 *
 */
@Slf4j
@Controller
public class HomeController extends BaseController {

	@Autowired
	private final AlgoSettingsService algoSettingsService;

	@Autowired
	public HomeController(AlgoSettingsService service) {
		this.algoSettingsService = service;
	}

	@GetMapping({ "/", View.INDEX_URL })
	public ModelAndView showIndexPage(ModelAndView modelAndView) {
		boolean debug = false;
		boolean showUploadOption = true;

		if (debug) {
			modelAndView.addObject("commonItemSet", MockUtil.getMockUserEventList());
			modelAndView.addObject("commonItemSetTitle", Constant.commonItemSetTitle);

			modelAndView.addObject("rareItemSet", MockUtil.getMockRareUserEventList());
			modelAndView.addObject("rareItemSetTitle", Constant.rareItemSetTitle);

			modelAndView.addObject("surveyMap", MockUtil.getMockTimeSomething());
			modelAndView.addObject("surveyMapMaxValue", MockUtil.getMockTimeSomethingMaxValue());
		}

		modelAndView.addObject("showUploadOption", showUploadOption);
		return view(View.INDEX_VIEW, modelAndView);
	}

	@PostMapping(View.INDEX_URL)
	public ModelAndView uploadFile(@RequestParam("filename") MultipartFile mFile, ModelAndView mav) {
		AlgoSettings algoSettings = this.algoSettingsService.getDefaultAlgoSettings();

		if (algoSettings.areAllAlgorithmsDisabled()) {
			// add error
		} else {
			LogFile logFile = LogFile.createFromMultipartFile(mFile, algoSettings.getVtsa());
			int sessionsCount = logFile.getUserSessionList().size();

			if (algoSettings.getLcm()) {
				Dataset dataset = new Dataset(logFile);
				Itemsets itemsets = new AlgoLCM().runAlgorithm(algoSettings.getLcmMinSup(), dataset);

				mav.addObject("commonItemSet", itemsets.getRelativeItemsets(sessionsCount));
				mav.addObject("commonItemSetTitle", Constant.commonItemSetTitle);
			}
			if (algoSettings.getRpg()) {
				Itemsets itemsets = new AlgoRPGrowth().runAlgorithm(logFile, algoSettings.getRpgMinSup(), algoSettings.getRpgMinRareSup());

				mav.addObject("rareItemSet", itemsets.getRelativeItemsets(sessionsCount));
				mav.addObject("rareItemSetTitle", Constant.rareItemSetTitle);
			}
			if (algoSettings.getVtsa()) {
				// do something nigga
			}
		}

		mav.addObject("upload_message", "You successfully uploaded " + mFile.getOriginalFilename() + "!");
		return view(View.INDEX_VIEW, mav);

	}

}
