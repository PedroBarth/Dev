package bot.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.inject.Inject;
import javax.inject.Named;

import bot.domain.Executives;
import bot.domain.Categories;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bot.domain.Message;
import bot.domain.Corporativa;
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

    final String id = this.jsonPath(json, "$.results.company.company_number");
    final String businessName = this.jsonPath(json, "$.results.company.name");
    final String address1 = null;
    final String address2 = null;
    final String address = this.jsonPath(json, "$.results.company.agent_address");
    final String city = null;
    final String province = null;
    final String postCode = null;
    final String region = null;
    final String country = this.jsonPath(json, "$.results.company.jurisdiction_code");
    final String phone = null;
    final String fax = null;
    final String mobile = null;
    final String email = null;
    final String website = null;
    final String facebook = null;
    final String twitter = null;
    final String linkedin = null;
    final String tiktok = null;
    final String instagram = null;
    final Integer latitude = null;
    final Integer longitude = null;
    final String language = null;
    final String nationalId = null;
    final String ceoName = null;
    final String ceoTitle = null;
    final Integer yearStarted = this.getDate(json, "$.results.company.created_at").getYear();
    final LocalDate dateStarted = this.getDate(json, "$.results.company.created_at");
    final Integer salesVolume = null;
    final String currency = null;
    final Integer salesVolumeSD = null;
    final LocalDate reportDate = null;
    final Integer employeesHere = this.jsonPath(json, "$.results.company.number_of_employees");
    final Integer employeesTotal = null;
    final String legalStatusCodeDescription = this.jsonPath(json, "$.results.company.current_status");
    final List<Categories> categories = this.getCategorie(json);
    final List<Executives> executives = this.getExecutives(json);

    return Optional.ofNullable(m.withPayload(
      this.gson.toJson(new Corporativa(id, businessName, address1, address2, address, city, province, postCode, region, country, phone, fax, mobile, email, website, facebook, twitter, linkedin, tiktok, instagram, latitude, longitude, language, nationalId, ceoName, ceoTitle, yearStarted, dateStarted, salesVolume, currency, salesVolumeSD, reportDate, employeesHere, employeesTotal, legalStatusCodeDescription, categories, executives))));

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

  private List<Categories> getCategories(String json) {
    final List<Categories> sources = new ArrayList<>();
    final List<Map<String, Object>> rawSources = this.jsonPath(json, "$.results.company.source");

    for (final Map<String, Object> source : rawSources) {
      json = this.gson.toJson(source);

      String name = this.jsonPath(json, "$.publisher");

      sources.add(new Categories(null, name));

    }
    return sources;
  }

  private List<Categories> getCategorie(String json) {
    final List<Categories> sources = new ArrayList<>();

    String name = this.jsonPath(json, "$.results.company.source.publisher");

    sources.add(new Categories(null, name));

    return sources;
  }

  private List<Executives> getExecutives(String json) {
    final List<Executives> officers = new ArrayList<>();
    final List<Map<String, Object>> rawOfficers = this.jsonPath(json, "$.results.company.officers");

    for (final Map<String, Object> officer : rawOfficers) {
      json = this.gson.toJson(officer);

      String fullName = this.jsonPath(json, "$.officer.name");
      String title = this.jsonPath(json, "$.officer.position");

      officers.add(new Executives(fullName, null, null, title, null, null));

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


