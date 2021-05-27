package com.revature.autosurvey.submissions.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.revature.autosurvey.submissions.beans.TrainingWeek;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class UtilitiesTest {
	
	@Test
	void getRandomNumberReturnsNumbersInRange() {
		List<Integer> numbersList = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			numbersList.add(Utilities.getRandomNumber(0, 999));
		}
		Boolean flag = true;
		for (int number : numbersList) {
			if (number < 0 || number > 999) {
				flag = false;
				break;
			}
		}
		assertEquals(true, flag);
	}
	
	@Test
	void bigSplitReturnsCorrectList() {
		String string = "this is a value,\"This is a, value wi,th a ,b,unch ,of com,mas ,i,n it\",and this is another value,and so is this";
		List<String> result = new ArrayList<>();
		result.add("this is a value");
		result.add("\"This is a, value wi,th a ,b,unch ,of com,mas ,i,n it\"");
		result.add("and this is another value");
		result.add("and so is this");
		assertEquals(result, Utilities.bigSplit(string));
	}
	
	@Test
	void timeLongFromStringReturnsTimeLong() {
		String string = "3/3/2020  14:08:17";
		Long timeLong = 1583244497000L;
		Long timeShorter = timeLong/1000;
		
		assertEquals(timeShorter, Utilities.timeLongFromString(string)/1000);
	}
	
	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnumA() {
		String weekString = "Week A";
		assertEquals(TrainingWeek.A, Utilities.getTrainingWeekFromString(weekString));
	}
	
	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnumB() {
		String weekString = "Week B";
		assertEquals(TrainingWeek.B, Utilities.getTrainingWeekFromString(weekString));
	}
	
	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum1() {
		String weekString = "Week 1";
		assertEquals(TrainingWeek.ONE, Utilities.getTrainingWeekFromString(weekString));
	}
	
	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum2() {
		String weekString = "Week 2";
		assertEquals(TrainingWeek.TWO, Utilities.getTrainingWeekFromString(weekString));
	}
	
	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum3() {
		String weekString = "Week 3";
		assertEquals(TrainingWeek.THREE, Utilities.getTrainingWeekFromString(weekString));
	}
	
	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum4() {
		String weekString = "Week 4";
		assertEquals(TrainingWeek.FOUR, Utilities.getTrainingWeekFromString(weekString));
	}
	
	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum5() {
		String weekString = "Week 5";
		assertEquals(TrainingWeek.FIVE, Utilities.getTrainingWeekFromString(weekString));
	}
	
	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum6() {
		String weekString = "Week 6";
		assertEquals(TrainingWeek.SIX, Utilities.getTrainingWeekFromString(weekString));
	}
	
	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum7() {
		String weekString = "Week 7";
		assertEquals(TrainingWeek.SEVEN, Utilities.getTrainingWeekFromString(weekString));
	}
	
	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum8() {
		String weekString = "Week 8";
		assertEquals(TrainingWeek.EIGHT, Utilities.getTrainingWeekFromString(weekString));
	}
	
	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum9() {
		String weekString = "Week 9";
		assertEquals(TrainingWeek.NINE, Utilities.getTrainingWeekFromString(weekString));
	}
	
	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum10() {
		String weekString = "Week 10";
		assertEquals(TrainingWeek.TEN, Utilities.getTrainingWeekFromString(weekString));
	}
	
	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum11() {
		String weekString = "Week 11";
		assertEquals(TrainingWeek.ELEVEN, Utilities.getTrainingWeekFromString(weekString));
	}
	
	@Test
	void getTrainingWeekFromStringReturnsTrainingWeekEnum12() {
		String weekString = "Week 12";
		assertEquals(TrainingWeek.TWELVE, Utilities.getTrainingWeekFromString(weekString));
	}
	
	@Test
	void getTrainingWeekFromStringReturnsNull() {
		String weekString = "Any Other Value";
		assertEquals(null, Utilities.getTrainingWeekFromString(weekString));
	}
	
	@Test
	void testReadStringFromFile() {
		FilePart filePart = Mockito.mock(FilePart.class);
		DataBuffer dataBuffer = Mockito.mock(DataBuffer.class);
		byte[] byteArray = "timeStamp".getBytes();
		ByteBuffer byteBuffer = ByteBuffer.allocate(byteArray.length);
		byteBuffer.put(byteArray);
		byteBuffer.position(0);
		Flux<DataBuffer> fluxDataBuffer = Flux.just(dataBuffer);
		when(filePart.content()).thenReturn(fluxDataBuffer);
		when(dataBuffer.readableByteCount()).thenReturn(byteArray.length);
		when(dataBuffer.read(any(byte[].class))).thenAnswer(invocation -> {
			Object[] args = invocation.getArguments();
			byteBuffer.get((byte[])args[0]);
			return null;
		});
		StepVerifier.create(Utilities.readStringFromFile(filePart))
		.expectNext("timeStamp")
		.expectComplete()
		.verify();
		
	}

}
