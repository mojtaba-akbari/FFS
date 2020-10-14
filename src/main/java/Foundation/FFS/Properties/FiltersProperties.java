package Foundation.FFS.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("filters.constant")
public class FiltersProperties {

}
