package bot.application;

import java.time.LocalDateTime;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;

import bot.domain.Message;
import bot.domain.RequestStatus;
import bot.port.input.InputMessageBroker;
import bot.port.output.LoaderRepository;
import bot.runnables.Scheduler;

@Named
@Scope("prototype")
public class OpenCorporatesScheduler extends Scheduler {

  private final InputMessageBroker queueInputMessageBroker;

  @Inject
  public OpenCorporatesScheduler(

    final InputMessageBroker queueInputMessageBroker,
    final LoaderRepository loaderRepository,
    @Value("${scheduler.input.limit.remaining:}") final int inputLimitRemaining,
    @Value("${scheduler.input.limit.repository:}") final int inputLimitRepository) {

    super(queueInputMessageBroker, loaderRepository, inputLimitRemaining, inputLimitRepository);
    this.queueInputMessageBroker = queueInputMessageBroker;
  }

  @Scheduled(cron = "00 41 20 * * *")
  public void scheduleTaskUsingCronExpression() {
    this.queueInputMessageBroker.reprocessMessage(new Message(new HashMap<String, String>(), "payload", RequestStatus.FOUND, LocalDateTime.now()));
  }
}