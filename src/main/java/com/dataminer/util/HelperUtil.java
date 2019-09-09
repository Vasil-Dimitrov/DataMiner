package com.dataminer.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dataminer.constant.Constant;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Vasil.Dimitrov^2
 *
 */
@Slf4j
public class HelperUtil {

	public static double round(double value, int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		}

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
	public static boolean generateFile(String fileBody, int sessionCount) {
		StringBuilder str = new StringBuilder();
		str.append("DATA MINER \n");
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyyг HH:mm:ss");
		Date date = new Date();
		str.append("Дата на анализ: " + dateFormat.format(date) + "\n");
		str.append("Общо анализирани потребителски сесии: " + sessionCount + "\n\n");
		str.append(fileBody);
		
		try {
			File file = new File(Constant.FILE_UPLOAD_DIR);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, false);
			fw.write(str.toString());
			fw.close();
		} catch (IOException e) {
			log.error("Exception was thrown trying to save file with data: \n" + str.toString(), e);
			return false;
		}
		
		return true;
	}
}
