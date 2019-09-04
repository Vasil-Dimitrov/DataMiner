package com.dataminer.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Vasil.Dimitrov^2
 *
 */
@Slf4j
public class HelperUtil {

	public static String fileToPath(String filename) {
		String userDir = System.getProperty("user.dir") + "\\test_files\\" + filename;
		// URL url = IndexController.class.getResource(filename);
		try {
			return java.net.URLDecoder.decode(userDir, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			log.debug(e.getMessage());
			return "ako";
		}
	}
}
