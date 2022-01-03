package bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@PropertySource({ "file:config/application.properties" })
@SpringBootApplication(scanBasePackages = {"bot"})
@EnableScheduling
public class BotOpenCorporatesApplication {

  public static void main(final String[] args) {
    SpringApplication.run(BotOpenCorporatesApplication.class, args);
  }
}
