package com.viettel.hstd.core.config;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class SelfRestTemplate extends RestTemplate {

    private HttpHeaders headers;

    public SelfRestTemplate() {
        headers = new HttpHeaders();
    }

    public void setToken(String token) {
        headers.add("Authorization", "Bearer " + token);
    }


    public HttpEntity<Object> httpRequest(Object object) {
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//        headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));

        HttpEntity<Object> request = new HttpEntity<Object>(object, headers);
        return request;
    }

    public HttpEntity<Object> httpRequest() {
        HttpEntity<Object> request = new HttpEntity<>(headers);
        return request;

    }

    @Override
    public <T> T getForObject(String url, Class<T> responseType, Object... urlVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(httpRequest(), responseType);
        HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(responseType,
                getMessageConverters());
        return execute(url, HttpMethod.GET, requestCallback, responseExtractor);
    }

    @Override
    public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(httpRequest(), responseType);
        HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(responseType,
                getMessageConverters());
        return execute(url, HttpMethod.GET, requestCallback, responseExtractor);
    }

    @Override
    public <T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables)
            throws RestClientException {
        return super.postForObject(url, httpRequest(request), responseType, uriVariables);
    }


    public <T> T deleteForObject(String url, Object... uriVariables) throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(httpRequest(), Boolean.class);
        HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(Boolean.class,
                getMessageConverters());
        return super.execute(url, HttpMethod.DELETE, requestCallback, responseExtractor);
    }


    public <T> T putForObject(String url, Object request, Class<T> responseType, Object... uriVariables)
            throws RestClientException {
        RequestCallback requestCallback = httpEntityCallback(httpRequest(request), responseType);
        HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(responseType,
                getMessageConverters());
        return super.execute(url, HttpMethod.PUT, requestCallback, responseExtractor);
    }


}
