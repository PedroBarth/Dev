package bot.domain;

public class  Executives {

  private final String FullName;
  private final String FirstName;
  private final String LastName;
  private final String Title;
  private final String Gender;
  private final String Language;

  public Executives(String fullName, String firstName, String lastName, String title, String gender, String language) {
    FullName = fullName;
    FirstName = firstName;
    LastName = lastName;
    Title = title;
    Gender = gender;
    Language = language;
  }

  public String getFullName() {
    return FullName;
  }

  public String getFirstName() {
    return FirstName;
  }

  public String getLastName() {
    return LastName;
  }

  public String getTitle() {
    return Title;
  }

  public String getGender() {
    return Gender;
  }

  public String getLanguage() {
    return Language;
  }
}
