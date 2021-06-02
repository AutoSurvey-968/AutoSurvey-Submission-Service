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
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
