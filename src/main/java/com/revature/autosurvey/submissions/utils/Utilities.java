package com.revature.autosurvey.submissions.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import com.revature.autosurvey.submissions.beans.TrainingWeek;

import reactor.core.publisher.Flux;

public class Utilities {

	private Utilities() {
		throw new IllegalStateException("Utility class");
	}

	private static Random rand = new SecureRandom();

	public static int getRandomNumber(int min, int max) {
		int bound = max - min;
		return rand.nextInt(bound) + min;
	}

	public static Long timeLongFromString(String timeString) {
		timeString = String.join(" ", timeString.split("\\s+"));
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/yyyy HH:mm:ss");
		LocalDateTime ldate = null;
		ldate = LocalDateTime.parse(timeString, dtf);
		int millis = getRandomNumber(0, 999);
		return ldate.toInstant(ZoneOffset.ofHours(0)).toEpochMilli() + millis;
	}

	public static List<String> bigSplit(String string) {
		String[] stringArr = string.split(",");
		List<String> stringList = new ArrayList<>(Arrays.asList(stringArr));
		Boolean truthFlag = true;
		while (Boolean.TRUE.equals(truthFlag)) {
			truthFlag = false;
			for (int i = 0; i < stringList.size(); i++) {
				if (!stringList.get(i).isEmpty() && stringList.get(i).charAt(0) == 34
						&& stringList.get(i).charAt(stringList.get(i).length() - 1) != 34) {
					stringList.set(i, stringList.get(i) + "," + stringList.get(i + 1));
					stringList.remove(i + 1);
					truthFlag = true;
					break;
				}
			}
		}
		return stringList;
	}

	public static Flux<String> readStringFromFile(FilePart file) {
		return file.content().map(buffer -> {
			byte[] bytes = new byte[buffer.readableByteCount()];
			buffer.read(bytes);
			DataBufferUtils.release(buffer);

			return new String(bytes);
		});
	}

	public static TrainingWeek getTrainingWeekFromString(String weekString) {
		switch (weekString) {
		case "Week A":
			return TrainingWeek.A;
		case "Week B":
			return TrainingWeek.B;
		case "Week 1":
			return TrainingWeek.ONE;
		case "Week 2":
			return TrainingWeek.TWO;
		case "Week 3":
			return TrainingWeek.THREE;
		case "Week 4":
			return TrainingWeek.FOUR;
		case "Week 5":
			return TrainingWeek.FIVE;
		case "Week 6":
			return TrainingWeek.SIX;
		case "Week 7":
			return TrainingWeek.SEVEN;
		case "Week 8":
			return TrainingWeek.EIGHT;
		case "Week 9":
			return TrainingWeek.NINE;
		case "Week 10":
			return TrainingWeek.TEN;
		default:
			return null;
		}
	}

}
