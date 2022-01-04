package bot.domain;


import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

public class Categories extends ValueObject {

  private final String code;
  private final String name;

  public Categories(final String code, final String name) {
    this.code = StringUtils.isNotBlank(code) ? code : null;
    this.name = StringUtils.isNotBlank(name) ? name : null;

  }

  public String getCode() { return code; }

  public String getName() { return name; }

}