package com.example.study.httptrace;

import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.data.annotation.Id;

import lombok.Getter;

public class HttpTraceWrapper {

	@Id
	private String id;

	@Getter
	private HttpTrace httpTrace;

	public HttpTraceWrapper(HttpTrace httpTrace) {
		this.httpTrace = httpTrace;
	}
}
