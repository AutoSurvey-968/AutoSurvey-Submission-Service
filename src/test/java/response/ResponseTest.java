package response;

import com.intuit.karate.junit5.Karate;

public class ResponseTest {

	@Karate.Test
    Karate testSample() {
		return Karate.run("response").relativeTo(getClass());
	}
}
