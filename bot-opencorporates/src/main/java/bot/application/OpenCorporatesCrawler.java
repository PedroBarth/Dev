package bot.application;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import bot.domain.RequestStatus;
import bot.utils.LocalDateAdapter;
import bot.utils.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.JsonPath;
import http.domain.MediaType;
import org.apache.http.impl.client.BasicCookieStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.context.annotation.Scope;

import bot.domain.CrawlerConfig;
import bot.domain.Message;
import bot.exceptions.ProxyException;
import bot.port.input.InputMessageBroker;
import bot.port.output.OutputMessageBroker;
import bot.repository.ProxyRepository;
import bot.runnables.Crawler;
import http.adapter.HttpGatewayImpl;
import http.domain.HttpMethod;
import http.domain.Proxy;
import http.port.HttpGateway;
import http.port.HttpResponse;

@Named
@Scope("prototype")
public class OpenCorporatesCrawler extends Crawler {


  private static final String CODE_SEARCH_PATH = "src/main/java/bot/application/OpenCorporatesCodeSearchPath";
  private static final String QUERY_SEARCH_PATH = "src/main/java/bot/application/OpenCorporatesQueryPath";
  private static final String BASE_URL = "https://opencorporates.com%s";
  private static final String QUERY_URL = "https://opencorporates.com/companies/%s?q=%s&utf8=✓";
  private static final String SEARCH_URL = "https://api.opencorporates.com%s";


  private final OutputMessageBroker outputMessageBroker;
  private final ProxyRepository proxyRepository;
  private final CrawlerConfig crawlerConfig;
  private Gson gson;

  @Inject
  public OpenCorporatesCrawler(
    final InputMessageBroker inputMessageBroker,
    final OutputMessageBroker minerOutputMessageBrokerImpl,
    final OutputMessageBroker loaderOutputMessageBrokerImpl,
    final LoggingApplicationService loggingApplicationService,
    final ProxyRepository proxyRepository,
    final CrawlerConfig crawlerConfig) {

    super(inputMessageBroker, minerOutputMessageBrokerImpl, loaderOutputMessageBrokerImpl, loggingApplicationService, crawlerConfig);

    this.proxyRepository = proxyRepository;
    this.outputMessageBroker = minerOutputMessageBrokerImpl;
    this.crawlerConfig = crawlerConfig;
    this.gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
      .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
  }

  @Override
  public Optional<Message> harvest(final Message m) throws Exception {
    final BasicCookieStore cookies = new BasicCookieStore();
    final Proxy proxy = this.retrieveProxy();
    final List<String> codeList = this.buildSearchList(CODE_SEARCH_PATH);
    final List<String> queryList = this.buildSearchList(QUERY_SEARCH_PATH);

    HttpResponse response = this.doHttpRequest(new HttpGatewayImpl(String.format(BASE_URL, "/users/sign_in"), HttpMethod.GET).cookieStore(cookies), this.crawlerConfig.getMaxRetryAttempts());

    var token = Jsoup.parse(response.getContent()).selectFirst("[name=\"authenticity_token\"]");

    response = this.doHttpRequest(new HttpGatewayImpl(String.format(BASE_URL, "/users/sign_in"), HttpMethod.POST).cookieStore(cookies).requestParameters(this.buildParamsLogin(token.attr("value"))).headers(this.generateHeadersLoginAction()), this.crawlerConfig.getMaxRetryAttempts());

    response = this.doHttpRequest(new HttpGatewayImpl(String.format(BASE_URL, "/users/account"), HttpMethod.GET).cookieStore(cookies).withProxy(proxy), this.crawlerConfig.getMaxRetryAttempts());
    // fim do login . . .

    for (String code : codeList) {
      for (String query : queryList) {
        response = this.doHttpRequest(new HttpGatewayImpl(String.format(QUERY_URL, code.split(">")[0], query), HttpMethod.GET).cookieStore(cookies).headers(this.generateHeadersSearchAction()).withProxy(proxy), this.crawlerConfig.getMaxRetryAttempts());

        while (true) {

          var checkPage = Jsoup.parse(response.getContent()).selectFirst("[class=\"oc-page-header\"]");
          if (checkPage != null) {
            if (checkPage.text().equals("Enterprise web account")) {
              System.out.println("(alert) You need a premium user account for this feature");
              break;
            }
          }
          var nextLink = Jsoup.parse(response.getContent()).selectFirst("[class=\"next next_page\"]");

          var rawCompanies = Jsoup.parse(response.getContent()).selectFirst("[id=\"companies\"]");
          if (rawCompanies == null){
            System.out.println("Div companies == null . . .");
            break;
          }

          var companies = rawCompanies.select(("[class=\"search-result company\"]"));

          for (Element comp : companies) {
            var companieID = comp.select(("[class=\"company_search_result\"]")).attr("href");

            response = this.doHttpRequest(new HttpGatewayImpl(String.format(SEARCH_URL, companieID), HttpMethod.GET).cookieStore(cookies).headers(this.generateHeadersSearchAPI()).withProxy(proxy), this.crawlerConfig.getMaxRetryAttempts());

            this.outputMessageBroker.sendMessage(m
              .withPayload(response.getContent())
              .withRequestStatus(RequestStatus.FOUND)
              .withParameters(this.buildMessageParams(m))
              .withMessageDate(LocalDateTime.now()));
            break;
          }

          if (nextLink == null){
            System.out.println("(alert) next link == null ...");
            break;
          }
          var link = nextLink.selectFirst("a").attr("href");
          response = this.doHttpRequest(new HttpGatewayImpl(String.format(BASE_URL, link), HttpMethod.GET).cookieStore(cookies).headers(this.generateHeadersSearchAction()).withProxy(proxy), this.crawlerConfig.getMaxRetryAttempts());

        }
      }
    }

    return Optional.empty();
  }

  private Proxy retrieveProxy() {
    //return null;
    return new Proxy("lum-customer-c_d565f260-zone-predictus_global_companies", "bxkw65ku0b5j", "zproxy.luminati.io", 22225);
  }

  private HttpResponse doHttpRequest(final HttpGateway request, int maxRetryAttempts) throws ProxyException {
    HttpResponse response = null;
    do
      try {
        final Proxy proxy = this.proxyRepository.get(this.thread()).orElse(this.retrieveProxy());
        response = request.withProxy(proxy)
          .connectionTimeout(this.crawlerConfig.getConnectionTimeout())
          .socketTimeout(this.crawlerConfig.getSocketTimeout())
          .charset(StandardCharsets.UTF_8)
          .timeToSleepBetweenRequests(0)
          .execute();
        if (response.getContent().contains("datasite-key")) throw new ProxyException("RECaptcha appears");
        this.validateResponse(response);
        this.proxyRepository.put(this.thread(), proxy);
        return response;
      } catch (final Exception e) {
        this.proxyRepository.remove(this.thread());
      } finally {
        maxRetryAttempts--;
      }
    while (maxRetryAttempts > 0);
    this.proxyRepository.remove(this.thread());
    throw new ProxyException("Proxy exception");
  }

  private String thread() {
    return Thread.currentThread().getName();
  }

  private Map<String, String> buildMessageParams(final Message m) {
    final Map<String, String> params = new HashMap<>(m.getParameters());
    return params;
  }

  private Map<String, String> buildParamsLogin(final String token) {
    final Map<String, String> params = new HashMap<>();
    params.put("authenticity_token", token);
    params.put("user[email]", "pedro-barth3@hotmail.com");
    params.put("user[password]", "aAbB1234");
    return params;
  }


  private Map<String, String> generateHeadersLoginAction() {
    final Map<String, String> headers = new HashMap<>(0);
    headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
    headers.put("Accept-Encoding", "gzip, deflate, br");
    headers.put("Accept-Language", "pt-BR,pt;q=0.8,en-US;q=0.5,en;q=0.3");
    headers.put("Connection", "keep-alive");
    headers.put("Content-Type", "application/x-www-form-urlencoded");
    headers.put("Host", "opencorporates.com");
    headers.put("Upgrade-Insecure-Requests", "1");
    headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0");
    return headers;
  }

  private Map<String, String> generateHeadersSearchAction() {
    final Map<String, String> headers = new HashMap<>(0);
    headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
    headers.put("Accept-Encoding", "gzip, deflate, br");
    headers.put("Accept-Language", "pt-BR,pt;q=0.8,en-US;q=0.5,en;q=0.3");
    headers.put("Connection", "keep-alive");
    headers.put("Host", "opencorporates.com");
    headers.put("Referer", "https://opencorporates.com/");
    headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0");
    return headers;
  }

  private Map<String, String> generateHeadersSearchAPI() {
    final Map<String, String> headers = new HashMap<>(0);
    headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
    headers.put("Accept-Encoding", "gzip, deflate, br");
    headers.put("Accept-Language", "pt-BR,pt;q=0.8,en-US;q=0.5,en;q=0.3");
    headers.put("Connection", "keep-alive");
    headers.put("Host", "api.opencorporates.com");
    headers.put("Referer", "https://opencorporates.com/");
    headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0");
    return headers;
  }

  private List<String> buildSearchList(final String path) throws IOException {
    List<String> searchList = new ArrayList<>();
    FileInputStream fstreamterms = new FileInputStream(path);
    BufferedReader bR = new BufferedReader(new InputStreamReader(fstreamterms));
    String params;
    while ((params = bR.readLine()) != null) {
      searchList.add(params);
    }
    bR.close();
    return searchList;
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
