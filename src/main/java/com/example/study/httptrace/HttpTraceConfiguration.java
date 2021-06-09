package com.example.study.httptrace;

import static java.util.Collections.*;

import java.net.URI;
import java.util.Map;

import org.bson.Document;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class HttpTraceConfiguration {

	// @Bean
	public HttpTraceRepository traceRepository() {
		return new InMemoryHttpTraceRepository();
	}

	@Bean
	public HttpTraceRepository springDataTraceRepository(HttpTraceWrapperRepository repository) {
		return new SpringDataHttpTraceRepository(repository);
	}

	static Converter<Document, HttpTraceWrapper> CONVERTER =
		new Converter<>() {
			@Override
			public HttpTraceWrapper convert(Document document) {
				Document httpTrace = document.get("httpTrace", Document.class);
				Document request = httpTrace.get("request", Document.class);
				Document response = httpTrace.get("response", Document.class);

				return new HttpTraceWrapper(new HttpTrace(
					new HttpTrace.Request(
						request.getString("method"),
						URI.create(request.getString("uri")),
						request.get("headers", Map.class),
						null),
					new HttpTrace.Response(
						response.getInteger("status"),
						response.get("headers", Map.class)),
					httpTrace.getDate("timestamp").toInstant(),
					null,null, httpTrace.getLong("timeTaken")));
			}
		};

	@Bean
	public MappingMongoConverter mappingMongoConverter(MongoMappingContext context) {
		MappingMongoConverter mappingMongoConverter =
			new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, context);

		mappingMongoConverter.setCustomConversions(new MongoCustomConversions(singletonList(CONVERTER)));
		return mappingMongoConverter;
	}
}
