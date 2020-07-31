package eve.tools.esi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

	private final static Logger log = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {

		traceRequest(request, body);
		ClientHttpResponse response = execution.execute(request, body);
		traceResponse(response);

		return response;
	}

	private void traceRequest(HttpRequest request, byte[] body) {
		log.info("==========================request begin==============================================");
		log.info("URI         : {}", request.getURI());
		log.info("Method      : {}", request.getMethod());
		log.info("Headers     : {}", request.getHeaders() );
		log.info("Request body: {}", new String(body, StandardCharsets.UTF_8));
		log.info("---request end---");
	}

	private void traceResponse(ClientHttpResponse response) throws IOException {
		log.info("---response begin---");
		log.info("Status Code : {}", response.getStatusCode());
		log.info("Status Text : {}", response.getStatusText());
		log.info("response body:{}", response.getBody());
		log.info("==========================response end================================================");
	}
}
