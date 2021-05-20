package com.revature.autosurvey.submissions.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revature.autosurvey.submissions.data.ResponseRepository;

@Service
public class ResponseServiceImpl implements ResponseService {
	
	private ResponseRepository responseRepo;
	
	@Autowired
	public void setResponseRepository(ResponseRepository responseRepo) {
		this.responseRepo = responseRepo;
	}

}
