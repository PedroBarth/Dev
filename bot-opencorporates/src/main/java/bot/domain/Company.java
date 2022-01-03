package bot.domain;

import java.time.LocalDate;
import java.util.List;

public class Company extends ValueObject {

  private final String name;
  private final String companyNumber;
  private final String jurisdictionCode;
  private final LocalDate incorporationDate;
  private final LocalDate dissolutionDate;
  private final String companyType;
  private final String registryURL;
  private final String branch;
  private final String branchStatus;
  private final Boolean inactive;
  private final String currentStatus;
  private final LocalDate creationDate;
  private final LocalDate updateDate;
  private final LocalDate retrieveDate;
  private final String openCorporatesURL;
  private final Source source;
  private final List<Officer> officers;

  public Company(String name, String companyNumber, String jurisdictionCode, LocalDate incorporationDate, LocalDate dissolutionDate, String companyType, String registryURL, String branch, String branchStatus, Boolean inactive, String currentStatus, LocalDate creationDate, LocalDate updateDate, LocalDate retrieveDate, String openCorporatesURL, Source source, List<Officer> officers) {
    this.name = name;
    this.companyNumber = companyNumber;
    this.jurisdictionCode = jurisdictionCode;
    this.incorporationDate = incorporationDate;
    this.dissolutionDate = dissolutionDate;
    this.companyType = companyType;
    this.registryURL = registryURL;
    this.branch = branch;
    this.branchStatus = branchStatus;
    this.inactive = inactive;
    this.currentStatus = currentStatus;
    this.creationDate = creationDate;
    this.updateDate = updateDate;
    this.retrieveDate = retrieveDate;
    this.openCorporatesURL = openCorporatesURL;
    this.source = source;
    this.officers = officers;
  }

  public String getName() {
    return name;
  }

  public String getCompanyNumber() {
    return companyNumber;
  }

  public String getJurisdictionCode() {
    return jurisdictionCode;
  }

  public LocalDate getIncorporationDate() {
    return incorporationDate;
  }

  public LocalDate getDissolutionDate() {
    return dissolutionDate;
  }

  public String getCompanyType() {
    return companyType;
  }

  public String getRegistryURL() {
    return registryURL;
  }

  public String getBranch() {
    return branch;
  }

  public String getBranchStatus() {
    return branchStatus;
  }

  public Boolean getInactive() {
    return inactive;
  }

  public String getCurrentStatus() {
    return currentStatus;
  }

  public LocalDate getCreationDate() {
    return creationDate;
  }

  public LocalDate getUpdateDate() {
    return updateDate;
  }

  public LocalDate getRetrieveDate() {
    return retrieveDate;
  }

  public String getOpenCorporatesURL() {
    return openCorporatesURL;
  }

  public Source getSource() {
    return source;
  }

  public List<Officer> getOfficers() {
    return officers;
  }

}