package bot.application;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;

import bot.port.output.LoaderRepository;
import bot.port.output.OutputMessageBroker;
import bot.runnables.Loader;

@Named
@Scope("prototype")
public class OpenCorporatesLoader extends Loader {

  @Inject
  public OpenCorporatesLoader(

    final OutputMessageBroker loaderOutputMessageBrokerImpl, final LoaderRepository repository,
    @Value("${output.loader.repository:false}") final boolean hasRepository) {
    super(loaderOutputMessageBrokerImpl, repository, hasRepository);
  }
}