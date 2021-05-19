package com.revature.autosurvey.submissions.service;

import com.revature.autosurvey.submissions.repositories.ResponseRepository;

public class ResponseService {
private ResponseRepository responseRepository;
	
	public void setResponseRepository(ResponseRepository responseRepository) {
		this.responseRepository = responseRepository;
	}
}
