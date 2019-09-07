package com.dataminer.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.dataminer.algorithm.lcm.AlgoLCM;
import com.dataminer.algorithm.lcm.Dataset;
import com.dataminer.algorithm.pattern.Itemsets;
import com.dataminer.algorithm.rpgrowth.AlgoRPGrowth;
import com.dataminer.constant.Constant;
import com.dataminer.constant.RequestAttribute;
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
		return view(View.INDEX_VIEW, modelAndView);
	}

	@PostMapping(View.INDEX_URL)
	public ModelAndView uploadFile(@RequestParam(RequestAttribute.FILE) MultipartFile mFile, ModelAndView mav) {
		AlgoSettings algoSettings = this.algoSettingsService.getDefaultAlgoSettings();
		boolean debug = true;

		if (algoSettings.areAllAlgorithmsDisabled()) {
			mav.addObject(RequestAttribute.ERROR_MSG, "Моля активирайте поне един алгоритъм от настройките!");
			return view(View.INDEX_VIEW, mav);
		}

		LogFile logFile = null;
		int sessionsCount = 0;
		if (!debug) {
			logFile = LogFile.createFromMultipartFile(mFile, algoSettings.getVtsa());
			sessionsCount = logFile.getUserSessionList().size();
		}

		if (algoSettings.getLcm()) {
			if (debug) {
				mav.addObject(RequestAttribute.LCM_DATA, MockUtil.getMockUserEventList());
				mav.addObject(RequestAttribute.LCM_TITLE, Constant.LCM_TITLE_TEXT);
			} else {
				Dataset dataset = new Dataset(logFile);
				Itemsets itemsets = new AlgoLCM().runAlgorithm(algoSettings.getLcmMinSup(), dataset);
				mav.addObject(RequestAttribute.LCM_DATA, itemsets.getRelativeItemsets(sessionsCount));
				mav.addObject(RequestAttribute.LCM_TITLE, Constant.LCM_TITLE_TEXT);
			}
		}
		if (algoSettings.getRpg()) {
			if (debug) {
				mav.addObject(RequestAttribute.RPG_DATA, MockUtil.getMockRareUserEventList());
				mav.addObject(RequestAttribute.RPG_TITLE, Constant.RPG_TITLE_TEXT);
			} else {
				Itemsets itemsets = new AlgoRPGrowth().runAlgorithm(logFile, algoSettings.getRpgMinSup(),
						algoSettings.getRpgMinRareSup());
				mav.addObject(RequestAttribute.RPG_DATA, itemsets.getRelativeItemsets(sessionsCount));
				mav.addObject(RequestAttribute.RPG_TITLE, Constant.RPG_TITLE_TEXT);
			}
		}
		if (algoSettings.getVtsa()) {
			if (debug) {
				mav.addObject(RequestAttribute.VTSA_DATA, MockUtil.getMockTimeSomething());
				mav.addObject(RequestAttribute.VTSA_TITLE, MockUtil.getMockTimeSomethingMaxValue());
			}
		}


		// VASIL TODO: add proper logic here
		String fileText = "This will be the text in a file!";
		try {
			File file = new File(Constant.FILE_UPLOAD_DIR);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, false);
			fw.write(fileText);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mav.addObject(RequestAttribute.SUCCESS_MSG, "Файл " + mFile.getOriginalFilename() + " бе успешно обработен!");
		return view(View.INDEX_VIEW, mav);

	}

	@GetMapping(View.DOWNLOAD_URL)
	@ResponseBody
	public FileSystemResource downloadFile() {
		File file = new File(Constant.FILE_UPLOAD_DIR);
		if (!file.exists()) {
			try {
				file.createNewFile();
				FileWriter fw = new FileWriter(file, false);
				fw.write("Файлът не бе намерен, моля опитайте да генерирате фалът отново чрез извършване на нов анализ от лог");
				fw.close();
				log.warn("Client tried retrieving file with analysis, but it was missing!");
			} catch (IOException e) {
				log.error("Exception occurred trying to recreate a misssing analysis file!", e);
			}
		}
		return new FileSystemResource(new File(Constant.FILE_UPLOAD_DIR));
	}
}
