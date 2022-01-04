package bot.domain;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;

public class Corporativa extends ValueObject {

  private final String id;
  private final String businessName;
  private final String address1;
  private final String address2;
  private final String address;
  private final String city;
  private final String province;
  private final String postCode;
  private final String region;
  private final String country;
  private final String phone;
  private final String fax;
  private final String mobile;
  private final String email;
  private final String website;
  private final String facebook;
  private final String twitter;
  private final String linkedin;
  private final String tiktok;
  private final String instagram;
  private final Integer latitude;
  private final Integer longitude;
  private final String language;
  private final String nationalId;
  private final String ceoName;
  private final String ceoTitle;
  private final Integer yearStarted;
  private final LocalDate dateStarted;
  private final Integer salesVolume;
  private final String currency;
  private final Integer salesVolumeSD;
  private final LocalDate reportDate;
  private final Integer employeesHere;
  private final Integer employeesTotal;
  private final String legalStatusCodeDescription;
  private final List<Categories> categories;
  private final List<Executives> executives;

  public Corporativa(final String id, final String businessName, final String address1, final String address2, final String address, final String city, final String province, final String postCode, final String region, final String country, final String phone, final String fax, final String mobile, final String email, final String website, final String facebook, final String twitter, final String linkedin, final String tiktok, final String instagram, final Integer latitude, final Integer longitude, final String language, final String nationalId, final String ceoName, final String ceoTitle, final Integer yearStarted, final LocalDate dateStarted, final Integer salesVolume, final String currency, final Integer salesVolumeSD, final LocalDate reportDate, final Integer employeesHere, final Integer employeesTotal, final String legalStatusCodeDescription, final List<Categories> categories, final List<Executives> executives) {

    this.id = id;
    this.businessName = StringUtils.isNotBlank(businessName) ? businessName : null;
    this.address1 = StringUtils.isNotBlank(address1) ? address1 : null;
    this.address2 = StringUtils.isNotBlank(address2) ? address2 : null;
    this.address = StringUtils.isNotBlank(address) ? address : null;
    this.city = StringUtils.isNotBlank(city) ? city : null;
    this.province = StringUtils.isNotBlank(province) ? province : null;
    this.postCode = StringUtils.isNotBlank(postCode) ? postCode : null;
    this.region = StringUtils.isNotBlank(region) ? region : null;
    this.country = StringUtils.isNotBlank(country) ? country : null;
    this.phone = StringUtils.isNotBlank(phone) ? phone : null;
    this.fax = StringUtils.isNotBlank(fax) ? fax : null;
    this.mobile = StringUtils.isNotBlank(mobile) ? mobile : null;
    this.email = StringUtils.isNotBlank(email) ? email : null;
    this.website = StringUtils.isNotBlank(website) ? website : null;
    this.facebook = StringUtils.isNotBlank(facebook) ? facebook : null;
    this.twitter = StringUtils.isNotBlank(twitter) ? twitter : null;
    this.linkedin = StringUtils.isNotBlank(linkedin) ? linkedin : null;
    this.tiktok = StringUtils.isNotBlank(tiktok) ? tiktok : null;
    this.instagram = StringUtils.isNotBlank(instagram) ? instagram : null;
    this.latitude = latitude;
    this.longitude = longitude;
    this.language = StringUtils.isNotBlank(language) ? language : null;
    this.nationalId = StringUtils.isNotBlank(nationalId) ? nationalId : null;
    this.ceoName = StringUtils.isNotBlank(ceoName) ? ceoName : null;
    this.ceoTitle = StringUtils.isNotBlank(ceoTitle) ? ceoTitle : null;
    this.yearStarted = yearStarted;
    this.dateStarted = dateStarted;
    this.salesVolume = salesVolume;
    this.currency = StringUtils.isNotBlank(currency) ? currency : null;
    this.salesVolumeSD = salesVolumeSD;
    this.reportDate = reportDate;
    this.employeesHere = employeesHere;
    this.employeesTotal = employeesTotal;
    this.legalStatusCodeDescription = StringUtils.isNotBlank(legalStatusCodeDescription) ? legalStatusCodeDescription : null;
    this.categories = categories;
    this.executives = executives;
  }


  public String getId() {
    return id;
  }

  public String getBusinessName() {
    return businessName;
  }

  public String getAddress1() {
    return address1;
  }

  public String getAddress2() {
    return address2;
  }

  public String getAddress() {
    return address;
  }

  public String getCity() {
    return city;
  }

  public String getProvince() {
    return province;
  }

  public String getPostCode() {
    return postCode;
  }

  public String getRegion() {
    return region;
  }

  public String getCountry() {
    return country;
  }

  public String getPhone() {
    return phone;
  }

  public String getCeoTitle() {
    return ceoTitle;
  }

  public String getFax() {
    return fax;
  }

  public String getMobile() {
    return mobile;
  }

  public String getEmail() {
    return email;
  }

  public String getWebsite() {
    return website;
  }

  public String getFacebook() {
    return facebook;
  }

  public String getTwitter() {
    return twitter;
  }

  public String getLinkedin() {
    return linkedin;
  }

  public String getTiktok() {
    return tiktok;
  }

  public String getInstagram() {
    return instagram;
  }

  public Integer getLatitude() {
    return latitude;
  }

  public Integer getLongitude() {
    return longitude;
  }

  public String getLanguage() {
    return language;
  }

  public String getNationalId() {
    return nationalId;
  }

  public String getCeoName() {
    return ceoName;
  }

  public Integer getYearStarted() {
    return yearStarted;
  }

  public LocalDate getDateStarted() {
    return dateStarted;
  }

  public Integer getSalesVolume() {
    return salesVolume;
  }

  public String getCurrency() {
    return currency;
  }

  public Integer getSalesVolumeSD() {
    return salesVolumeSD;
  }

  public LocalDate getReportDate() {
    return reportDate;
  }

  public Integer getEmployeesHere() {
    return employeesHere;
  }

  public Integer getEmployeesTotal() {
    return employeesTotal;
  }

  public String getLegalStatusCodeDescription() {
    return legalStatusCodeDescription;
  }

  public List<Categories> getCategories() {
    return categories;
  }

  public List<Executives> getExecutives() {
    return executives;
  }

}