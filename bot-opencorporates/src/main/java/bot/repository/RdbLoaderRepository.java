package bot.repository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bot.domain.Message;
import bot.port.output.LoaderRepository;

@Named
public class RdbLoaderRepository implements LoaderRepository {

  private final Logger log;

  @Inject
  public RdbLoaderRepository() {
    this.log = LoggerFactory.getLogger(this.getClass());
  }

  @Override
  public Optional<List<Message>> gatherInput(final int limit) {
    final List<Message> messages = new ArrayList<>(0);
    return Optional.ofNullable(messages);
  }

  @Override
  public void save(final Message m) {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter("output.jsonl", true));
      writer.write(m.getPayload().concat("\n"));
      writer.close();
    } catch (final Exception e) {
      this.log.error(e.getMessage());
    }
  }

  @Override
  public void updateInput(final Message m) {
  }

}