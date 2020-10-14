package Foundation.FFS.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("spring.scrapper")
public class ScrapperProperties {
	private String WatchMarketUrl;
	private String Method;
	private int BufferSize;
	
	public String getWatchMarketUrl() {
		return WatchMarketUrl;
	}

	public void setWatchMarketUrl(String watchMarketUrl) {
		WatchMarketUrl = watchMarketUrl;
	}

	public String getMethod() {
		return Method;
	}

	public void setMethod(String method) {
		Method = method;
	}

	public int getBufferSize() {
		return BufferSize;
	}

	public void setBufferSize(int bufferSize) {
		BufferSize = bufferSize;
	}
	
}
