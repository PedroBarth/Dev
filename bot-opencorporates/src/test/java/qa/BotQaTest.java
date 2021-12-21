package qa;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import bot.BotInfobelApplication;
import bot.application.InfobelCrawler;
import bot.application.InfobelMiner;
import bot.domain.Message;
import bot.domain.RequestStatus;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = BotInfobelApplication.class)
class BotQaTest {

  @Autowired
  private InfobelCrawler crawler;

  @Autowired
  private InfobelMiner miner;

  Optional<Message> harvestedCrawler = Optional.empty();
  Optional<Message> harvestedMiner = Optional.empty();

  @Test
  void shouldFindJobOcupation() throws Exception {
    this.harvestedCrawler = this.crawler.
      harvest(new Message("domainTest", "applicationTest", Collections.singletonMap("input", "08656988820218140301"), "", RequestStatus.FOUND, LocalDateTime.now()));

    this.harvestedCrawler.ifPresent(message -> this.harvestedMiner = this.miner.harvest(message));

    if (this.harvestedMiner.isPresent())
      return;
    fail();
  }

}