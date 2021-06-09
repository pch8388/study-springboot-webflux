package com.example.study.httptrace;

import java.util.stream.Stream;

import org.springframework.data.repository.Repository;

public interface HttpTraceWrapperRepository extends Repository<HttpTraceWrapper, String> {

	Stream<HttpTraceWrapper> findAll();

	void save(HttpTraceWrapper trace);
}
