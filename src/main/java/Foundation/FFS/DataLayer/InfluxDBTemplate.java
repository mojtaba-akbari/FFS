package Foundation.FFS.DataLayer;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import Foundation.FFS.Properties.InfluxDBProperties;

@Component
@EnableConfigurationProperties(InfluxDBProperties.class)
public class InfluxDBTemplate {
	
	final private InfluxDB InfluxDBConnectionHolder;
	
	@Autowired
	final private InfluxDBProperties InfluxDBConnectionPropertiesHolder;


	public InfluxDBTemplate(final InfluxDBProperties properties) {
		this.InfluxDBConnectionHolder=InfluxDBFactory.connect(properties.getUrl(),properties.getUsername(),properties.getPassword());
		this.InfluxDBConnectionPropertiesHolder=properties;
	}

	public InfluxDB getInfluxDBConnectionHolder() {
		return InfluxDBConnectionHolder;
	}
	
	public InfluxDBProperties getInfluxDBConnectionPropertiesHolder() {
		return InfluxDBConnectionPropertiesHolder;
	}

	public void WriteInstance(BatchPoints bp) {
		InfluxDBConnectionHolder.write(bp);
	}
	
	public QueryResult ExecQuery(Query query) {
		return InfluxDBConnectionHolder.query(query);
	}
	
	public void CloseInstance() {
		InfluxDBConnectionHolder.close();
	}
}
