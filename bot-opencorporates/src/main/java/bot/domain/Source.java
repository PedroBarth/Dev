package bot.domain;


import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

public class Source extends ValueObject {

  private final String publisher;
  private final String url;
  private final String terms;
  private final String termsUrl;
  private final LocalDate retrievedAt;


  public Source(final String publisher, final String url, final String terms, final String termsUrl, final LocalDate retrievedAt) {
    this.publisher = publisher;
    this.url = url;
    this.terms = terms;
    this.termsUrl = termsUrl;
    this.retrievedAt = retrievedAt;
  }

  public String getPublisher() {
    return publisher;
  }

  public String getUrl() {
    return url;
  }

  public String getTerms() {
    return terms;
  }

  public String getTermsUrl() {
    return termsUrl;
  }

  public LocalDate getRetrievedAt() {
    return retrievedAt;
  }
}