package com.revature.autosurvey.submissions.beans;

import lombok.Data;

@Data
public class TokenVerifierRequest {
	private String token;
	private boolean returnSecureToken;
	
}
