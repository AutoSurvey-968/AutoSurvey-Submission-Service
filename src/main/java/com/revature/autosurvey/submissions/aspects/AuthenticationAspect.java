package com.revature.autosurvey.submissions.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.revature.autosurvey.submissions.utils.FirebaseUtil;

import reactor.core.publisher.Mono;

@Component
@Aspect
public class AuthenticationAspect {

	private FirebaseUtil firebaseUtil;

	@Around("@annotation(Authenticated)")
	public Object authenticate(ProceedingJoinPoint pjp) {
		return firebaseUtil.getDetailsFromCustomToken((String) pjp.getArgs()[0])
				.switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)).map(token -> {
					Object result = null;
					try {
						result = pjp.proceed();
					} catch (Throwable e) {
						result = ResponseEntity.badRequest().body(e);
					}
					return result;
				}));
	}
}
