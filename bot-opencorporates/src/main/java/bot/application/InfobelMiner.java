package bot.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.inject.Inject;
import javax.inject.Named;

import bot.domain.Categories;
import bot.domain.Executives;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
public class InfobelMiner extends Miner {

  private final Gson gson;

  @Inject
  public InfobelMiner(

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

    final String id = this.jsonPath(json, "$.UniqueID");
    final String businessName = this.jsonPath(json, "$.BusinessName");
    final String address1 = this.jsonPath(json, "$.Address1");
    final String address2 = this.jsonPath(json, "$.Address2");
    final String address = this.jsonPath(json, "$.Address");
    final String city = this.jsonPath(json, "$.City");
    final String province = this.jsonPath(json, "$.Province");
    final String postCode = this.jsonPath(json, "$.PostCode");
    final String region = this.jsonPath(json, "$.Region");
    final String country = this.jsonPath(json, "$.Country");
    final String phone = this.jsonPath(json, "$.Phone");
    final String fax = this.jsonPath(json, "$.Fax");
    final String mobile = this.jsonPath(json, "$.Mobile");
    final String email = this.jsonPath(json, "$.Email");
    final String website = this.jsonPath(json, "$.Website");
    final String facebook = this.jsonPath(json, "$.Facebook");
    final String twitter = this.jsonPath(json, "$.Twitter");
    final String linkedin = this.jsonPath(json, "$.Linkedin");
    final String tiktok = this.jsonPath(json, "$.Tiktok");
    final String instagram = this.jsonPath(json, "$.Instagram");
    final Integer latitude = this.getLatitude(json);
    final Integer longitude = this.getLongitude(json);
    final String language = this.jsonPath(json, "$.Language");
    final String nationalId = this.jsonPath(json, "$.NationalID");
    final String ceoName = this.jsonPath(json, "$.CEOName");
    final String ceoTitle = this.jsonPath(json, "$.CEOTitle");
    final LocalDate yearStarted = null;
    final LocalDate dateStarted = null;
    final Integer salesVolume = Integer.parseInt(this.jsonPath(json, "$.SalesVolume").toString());
    final String currency = this.jsonPath(json, "$.Currency");
    final Integer salesVolumeSD = Integer.parseInt(this.jsonPath(json, "$.SalesVolumeDollars").toString());
    final LocalDate reportDate = this.getReportDate(json);
    final Integer employeesHere = Integer.parseInt(this.jsonPath(json, "$.EmployeesHere").toString());
    final Integer employeesTotal = Integer.parseInt(this.jsonPath(json, "$.EmployeesTotal").toString());
    final String legalStatusCodeDescription = this.jsonPath(json, "$.LegalStatusCodeDescription");
    final List<Categories> categories = this.getCategories(json);
    final List<Executives> executives = this.getExecutives(json);


    return Optional.ofNullable(m.withPayload(
      this.gson.toJson(new Corporativa(id, businessName, address1, address2, address, city, province, postCode, region, country, phone, fax, mobile, email, website, facebook, twitter, linkedin, tiktok, instagram, latitude, longitude, language, nationalId, ceoName, ceoTitle, yearStarted, dateStarted, salesVolume, currency, salesVolumeSD, reportDate, employeesHere, employeesTotal, legalStatusCodeDescription, categories, executives))));
  }

  private LocalDate getReportDate(String json) {
    try {
      var RawDate = this.jsonPath(json, "$.ReportDate");
      LocalDate dateTime = LocalDate.parse(RawDate.toString(),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      return dateTime;
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

  private List<Categories> getCategories(String json) {
    final List<Categories> categories = new ArrayList<>();

    final List<Map<String, Object>> RawCategories = this.jsonPath(json, "$.InfobelCategories");

    for (final Map<String, Object> categorie : RawCategories) {
      json = this.gson.toJson(categorie);
      var name = this.jsonPath(json, "$.Name");
      var code = this.jsonPath(json, "$.Code");

      categories.add(new Categories(name.toString(), code.toString()));

    }
    return categories;
  }

  private List<Executives> getExecutives(String json) {
    final List<Executives> executives = new ArrayList<>();

    final List<Map<String, Object>> RawExecutives = this.jsonPath(json, "$.Executives");

    for (final Map<String, Object> executive : RawExecutives) {
      json = this.gson.toJson(executive);
      var Fullname = this.jsonPath(json, "$.FullName");
      var Firstname = this.jsonPath(json, "$.FirstName");
      var Lastname = this.jsonPath(json, "$.LastName");
      var Title = this.jsonPath(json, "$.Title");
      var Gender = this.jsonPath(json, "$.Gender");

      executives.add(new Executives(Fullname.toString(), Firstname.toString(), Lastname.toString(), Title.toString(), Gender.toString(), null));

    }

    return executives;
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


