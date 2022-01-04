package bot.domain;

import java.time.LocalDate;

public class Executives {

  private final String fullName;
  private final String firstName;
  private final String lastName;
  private final String title;
  private final String gender;
  private final String language;

  public Executives(String fullName, String firstName, String lastName, String title, String gender, String language) {
    this.fullName = fullName;
    this.firstName = firstName;
    this.lastName = lastName;
    this.title = title;
    this.gender = gender;
    this.language = language;
  }

  public String getFullName() {
    return fullName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getTitle() {
    return title;
  }

  public String getGender() {
    return gender;
  }

  public String getLanguage() {
    return language;
  }

}
