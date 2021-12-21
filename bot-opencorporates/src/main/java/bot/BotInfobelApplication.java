package bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@PropertySource({ "file:config/application.properties" })
@SpringBootApplication(scanBasePackages = { "bot", "neoway" })
@EnableScheduling
public class BotInfobelApplication {

  public static void main(final String[] args) {
    SpringApplication.run(BotInfobelApplication.class, args);
  }
}
