package Foundation.FFS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"Foundation.FFS","Foundation.FFS.DataLayer","Foundation.FFS.WebSockets","Foundation.FFS.Configure","Foundation.FFS.Filters","Foundation.FFS.Services"})
@SpringBootApplication
public class FFSApplication implements CommandLineRunner {
	
	@Autowired
	private FFSBootStrapper FFSBootStrapper;
	
	public static void main(String[] args) {
		SpringApplication.run(FFSApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		FFSBootStrapper.run();
	}

}
