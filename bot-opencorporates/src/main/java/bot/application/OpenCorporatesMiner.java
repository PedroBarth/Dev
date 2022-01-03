package bot.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.inject.Inject;
import javax.inject.Named;

import bot.domain.Officer;
import bot.domain.Source;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bot.domain.Message;
import bot.domain.Company;
import bot.port.output.LoaderRepository;
import bot.port.output.OutputMessageBroker;
import bot.runnables.Miner;
import bot.utils.LocalDateAdapter;
import bot.utils.LocalDateTimeAdapter;

@Named
@Scope("prototype")
public class OpenCorporatesMiner extends Miner {

  private final Gson gson;

  @Inject
  public OpenCorporatesMiner(

    final OutputMessageBroker minerOutputMessageBrokerImpl,
    final OutputMessageBroker loaderOutputMessageBrokerImpl,
    final LoaderRepository repository) {

    super(minerOutputMessageBrokerImpl, loaderOutputMessageBrokerImpl, repository);
    this.gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
      .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).disableHtmlEscaping().create();
  }

  @Override
  public Optional<Message> harvest(final Message m) {
    if (StringUtils.isEmpty(m.getPayload()))
      return Optional.of(m);


    var json = m.getPayload();

    final String name = this.jsonPath(json, "$.results.company.name");
    final String companyNumber = this.jsonPath(json, "$.results.company.company_number");
    final String jurisdictionCode = this.jsonPath(json, "$.results.company.jurisdiction_code");
    final LocalDate incorporationDate = this.getDate(json, "$.results.company.incorporation_date");
    final LocalDate dissolutionDate = this.getDate(json, "$.results.company.dissolution_date");
    final String companyType = this.jsonPath(json, "$.results.company.company_type");
    final String registryURL = this.jsonPath(json, "$.results.company.registry_url");
    final String branch = this.jsonPath(json, "$.results.company.branch");
    final String branchStatus = this.jsonPath(json, "$.results.company.branch_status");
    final Boolean inactive = this.jsonPath(json, "$.results.company.inactive");
    final String currentStatus = this.jsonPath(json, "$.results.company.current_status");
    final LocalDate creationDate = this.getDate(json, "$.results.company.created_at");
    final LocalDate updateDate = this.getDate(json, "$.results.company.updated_at");
    final LocalDate retrieveDate = this.getDate(json, "$.results.company.retrieved_at");
    final String openCorporatesURL = this.jsonPath(json, "$.results.company.opencorporates_url");
    final Source source = this.getSources(json);
    final List<Officer> officers = this.getOfficers(json);

    return Optional.ofNullable(m.withPayload(
      this.gson.toJson(new Company(name, companyNumber, jurisdictionCode, incorporationDate, dissolutionDate, companyType, registryURL, branch, branchStatus, inactive, currentStatus, creationDate, updateDate, retrieveDate, openCorporatesURL, source, officers))));
  }

  private LocalDate getDate(String json, String path) {
    try {
      String rawDate = this.jsonPath(json, path);
      rawDate = rawDate.split("T")[0];

      LocalDate date = LocalDate.parse(rawDate,
              DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      return date;
    } catch (Exception e) {
      return null;
    }
  }


  private Integer getLongitude(String json) {
    try {
      var RawLatitude = this.jsonPath(json, "$.Longitude");

      return Integer.parseInt(RawLatitude.toString().replaceAll("[^\\d]", ""));
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private Integer getLatitude(String json) {
    try {
      var RawLatitude = this.jsonPath(json, "$.Latitude");

      return Integer.parseInt(RawLatitude.toString().replaceAll("[^\\d]", ""));
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private Source getSources(String json) {

    String publisher = this.jsonPath(json, "$.results.company.source.publisher");
    String url = this.jsonPath(json, "$.results.company.source.url");
    String terms = this.jsonPath(json, "$.results.company.source.terms");
    String termsURL = this.jsonPath(json, "$.results.company.source.terms_url");
    LocalDate retrievedAt = this.getDate(json, "$.results.company.retrieved_at");

    Source source = new Source(publisher, url, terms, termsURL, retrievedAt);

    return source;
  }

  private List<Officer> getOfficers(String json) {
    final List<Officer> officers = new ArrayList<>();
    final List<Map<String, Object>> rawOfficers = this.jsonPath(json, "$.results.company.officers");

    for (final Map<String, Object> officer : rawOfficers) {
      json = this.gson.toJson(officer);

      Integer id = this.jsonPath(json, "$.officer.id");
      String name = this.jsonPath(json, "$.officer.name");
      String position = this.jsonPath(json, "$.officer.position");
      String uid = this.jsonPath(json, "$.officer.uid");
      LocalDate startDate = this.getDate(json, "$.officer.start_date");
      LocalDate endDate = this.getDate(json, "$.officer.end_date");
      String openCorporatesURL = this.jsonPath(json, "$.officer.opencorporates_url");
      String occupation = this.jsonPath(json, "$.officer.occupation");
      String inactive = this.jsonPath(json, "$.officer.inactive");
      String currentStatus = this.jsonPath(json, "$.officer.current_status");

      officers.add(new Officer(id, name, position, uid, startDate, endDate, openCorporatesURL, occupation, inactive, currentStatus));

    }
    return officers;
  }


  private <T> T jsonPath(final String json, final String path) {
    try {
      return JsonPath.read(json, path);
    } catch (Exception e) {
      this.getLog().error(e.getMessage());
    }
    return null;
  }
}


