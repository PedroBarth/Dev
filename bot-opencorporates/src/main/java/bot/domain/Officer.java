package bot.domain;

import java.time.LocalDate;

public class Officer {

  private final Integer id;
  private final String name;
  private final String position;
  private final String uid;
  private final LocalDate startDate;
  private final LocalDate endDate;
  private final String openCorporatesURL;
  private final String occupation;
  private final String inactive;
  private final String currentStatus;

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getPosition() {
    return position;
  }

  public String getUid() {
    return uid;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public String getOpenCorporatesURL() {
    return openCorporatesURL;
  }

  public String getOccupation() {
    return occupation;
  }

  public String getInactive() {
    return inactive;
  }

  public String getCurrentStatus() {
    return currentStatus;
  }

  public Officer(Integer id, String name, String position, String uid, LocalDate startDate, LocalDate endDate, String openCorporatesURL, String occupation, String inactive, String currentStatus) {
    this.id = id;
    this.name = name;
    this.position = position;
    this.uid = uid;
    this.startDate = startDate;
    this.endDate = endDate;
    this.openCorporatesURL = openCorporatesURL;
    this.occupation = occupation;
    this.inactive = inactive;
    this.currentStatus = currentStatus;
  }


}
