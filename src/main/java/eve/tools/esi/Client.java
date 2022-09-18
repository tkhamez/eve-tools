package eve.tools.esi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import eve.tools.esi.model.EsiError;

/**
 * All methods return null on error and error is logged to lastError property.
 */
@Service
public class Client {

	private final Logger log = LoggerFactory.getLogger(Client.class);

	@Autowired
	private RestTemplate restTemplate;

	private EsiError lastError;

	private Integer pages;

	public EsiError getLastError() {
		return lastError;
	}

	public Integer getPages() {
		return pages;
	}

	public <T> T get(Class<T> valueType, String url) {
		return request(valueType, url, HttpMethod.GET, new HttpHeaders(), null);
	}

	public <T> T get(Class<T> valueType, String url, HttpHeaders headers) {
		return request(valueType, url, HttpMethod.GET, headers, null);
	}

	public <T> T post(Class<T> valueType, String url, String body) {
		return request(valueType, url, HttpMethod.POST, new HttpHeaders(), body);
	}

	public <T> T post(Class<T> valueType, String url, HttpHeaders headers, String body) {
		return request(valueType, url, HttpMethod.POST, headers, body);
	}

	public <T> T post(Class<T> valueType, String url, HttpHeaders headers, MultiValueMap<String, String> body) {
		return request(valueType, url, HttpMethod.POST, headers, body);
	}

	private <T> T request(Class<T> type, String url, HttpMethod method, HttpHeaders headers, Object body) {
		headers.set("User-Agent", "EVE Tools/0.1.0-SNAPSHOT (https://github.com/tkhamez/eve-tools)");

		HttpEntity<Object> requestEntity;
		if (body != null) {
			requestEntity = new HttpEntity<>(body, headers);
		} else {
			requestEntity = new HttpEntity<>(headers);
		}

		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(new LoggingRequestInterceptor());
		//restTemplate.setInterceptors(interceptors);

		ResponseEntity<String> response;
		try {
			response = restTemplate.exchange(url, method, requestEntity, String.class);
		} catch(RestClientException e) {
			log.error("Catched exception: ", e);
			return null;
		}

		List<String> headerWarning = response.getHeaders().get("warning");
		if (headerWarning != null && headerWarning.size() == 1) {
			log.warn(url + " warning: " + headerWarning.get(0)); // e. g. "199 - This endpoint has been updated."
		}

		List<String> headerPages = response.getHeaders().get("X-Pages");
		if (headerPages != null && headerPages.size() == 1) {
			pages = Integer.valueOf(headerPages.get(0));
		} else {
			pages = 0;
		}

		ObjectMapper mapper = new ObjectMapper();

		T result = null;
		lastError = null;
		if (response.getStatusCode() == HttpStatus.OK) {
			try {
				result = mapper.readValue(response.getBody(), type);
			} catch (IOException e1) {
				lastError = new EsiError();
				lastError.get_other().put(e1.getClass().getName(), e1.getMessage());
			}
		} else {
			String responseBody = response.getBody();
			if (responseBody == null) {
				lastError = new EsiError();
			} else {
				try {
					lastError = mapper.readValue(responseBody, EsiError.class);
				} catch (IOException e2) {
					lastError = new EsiError();
				}
			}
			lastError.set_http_status_code(response.getStatusCode().value());
			lastError.set_body(responseBody);
		}

		if (lastError != null) {
			lastError.set_url(url);
			if (lastError.getError() == null) {
				log.error("Api Error: " + lastError.toString());
			} else if (response.getStatusCode() != HttpStatus.FORBIDDEN && // 403 is part of the API
				(
					// The refresh token is expired
					response.getStatusCode() != HttpStatus.BAD_REQUEST &&
					lastError.getError().equals("invalid_token")
				)
			) {
				log.error("Api Error: " + lastError.toString());
			}
		}

		return result;
	}
}
