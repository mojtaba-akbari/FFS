package Foundation.FFS.Services;

import java.net.http.HttpHeaders;
import java.util.HashMap;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import Foundation.FFS.Properties.ScrapperProperties;
import ch.qos.logback.core.util.Duration;
import reactor.core.publisher.Mono;

@Component
@Scope("prototype")
@EnableConfigurationProperties(ScrapperProperties.class)
public class FFSScrapper implements Runnable {
	// Implement More Cache //
	// Any Instance Should Make Again If You Need Change URL //
	
	private String CacheHolder;
	


	private String FixUrl;
	private HttpMethod Method;
	private WebClient Webclient;
	
	private ResponseSpec CacheResponse;

	public FFSScrapper(final ScrapperProperties properties)  {
		this.FixUrl=properties.getWatchMarketUrl();
		this.Method=properties.getMethod().equals("GET")? HttpMethod.GET:null;
		this.Webclient=WebClient.builder()
				.baseUrl(this.FixUrl)
				.codecs(configurer->configurer
				.defaultCodecs()
				.maxInMemorySize(properties.getBufferSize())).build();
	}
	
	// ReUse IoC Injection Class Scraper That Needed //
	public void FFSScrapperNewInstance(String url,String method,int buffsize)  {
		this.FixUrl=url;
		this.Method=method.equals("GET")? HttpMethod.GET:null;
		this.Webclient=WebClient.builder()
				.baseUrl(this.FixUrl)
				.codecs(configurer->configurer
				.defaultCodecs()
				.maxInMemorySize(buffsize)).build();
	}
	
	public Mono<String> ScrapperResultTextHtmlData(int timeoutresponse) {
		try {

			this.CacheResponse=this.Webclient
					.get()
					.accept(MediaType.TEXT_HTML)
					.retrieve();

			return CacheResponse.bodyToMono(String.class)
					.timeout(java.time.Duration.ofMillis(timeoutresponse)); // I SET For INIT DATA LOAD //
		}
		catch(Exception ex)
		{
			//ex.printStackTrace();
			return null;
		}
	}
	
	public Mono<String> ScrapperResult() {
		// No Throws //
		try {
			return this.Webclient
					.get()
					.accept(MediaType.APPLICATION_OCTET_STREAM)
					.retrieve().bodyToMono(String.class);

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public String getCacheHolder() {
		return CacheHolder;
	}

	public void setCacheHolder(String cacheHolder) {
		CacheHolder = cacheHolder;
	}
	
	public String getFixUrl() {
		return FixUrl;
	}

	public void setFixUrl(String fixUrl) {
		FixUrl = fixUrl;
	}

	public HttpMethod getMethod() {
		return Method;
	}

	public void setMethod(HttpMethod method) {
		Method = method;
	}
	
	@Override
	public void run() {

	}
	
}
