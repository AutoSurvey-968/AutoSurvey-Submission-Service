package com.revature.autosurvey.submissions.service;

import org.springframework.stereotype.Service;

import com.revature.autosurvey.submissions.data.ResponseRepository;

@Service
public class ResponseService {
private ResponseRepository responseRepository;
	
	public void setResponseRepository(ResponseRepository responseRepository) {
		this.responseRepository = responseRepository;
	}
}
