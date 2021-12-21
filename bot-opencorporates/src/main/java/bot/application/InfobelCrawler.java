package bot.application;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
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
import neoway.service.NeowayService;

@Named
@Scope("prototype")
public class InfobelCrawler extends Crawler {


  private static final String SEARCH_PATH = "src/main/java/bot/application/InfobelSearchPath";
  private static final String BASE_URL = "https://use.infobelpro.com%s";


  private final OutputMessageBroker outputMessageBroker;
  private final ProxyRepository proxyRepository;
  private final CrawlerConfig crawlerConfig;
  private final NeowayService neowayService;
  private Gson gson;

  @Inject
  public InfobelCrawler(
    final InputMessageBroker inputMessageBroker,
    final OutputMessageBroker minerOutputMessageBrokerImpl,
    final OutputMessageBroker loaderOutputMessageBrokerImpl,
    final LoggingApplicationService loggingApplicationService,
    final ProxyRepository proxyRepository,
    final NeowayService neowayService,
    final CrawlerConfig crawlerConfig) {

    super(inputMessageBroker, minerOutputMessageBrokerImpl, loaderOutputMessageBrokerImpl, loggingApplicationService, crawlerConfig);

    this.proxyRepository = proxyRepository;
    this.outputMessageBroker = minerOutputMessageBrokerImpl;
    this.neowayService = neowayService;
    this.crawlerConfig = crawlerConfig;
    this.gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
      .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
  }

  @Override
  public Optional<Message> harvest(final Message m) throws Exception {
    final BasicCookieStore cookies = new BasicCookieStore();
    final Proxy proxy = this.retrieveProxy();

    final List<String> searchList = new ArrayList<>();
    this.buildSearchList(searchList);

    HttpResponse response = this.doHttpRequest(new HttpGatewayImpl(String.format(BASE_URL, "/Account/Login"), HttpMethod.GET).cookieStore(cookies).withProxy(proxy), this.crawlerConfig.getMaxRetryAttempts());

    var token = Jsoup.parse(response.getContent()).selectFirst("[name=\"__RequestVerificationToken\"]");

    response = this.doHttpRequest(new HttpGatewayImpl(String.format(BASE_URL, "/Account/ValidateLogin?PreCheckout=False"), HttpMethod.POST).cookieStore(cookies).withProxy(proxy).requestParameters(this.buildParamsLogin(token.attr("value"))).headers(this.generateHeadersLoginAction()), this.crawlerConfig.getMaxRetryAttempts());

    response = this.doHttpRequest(new HttpGatewayImpl(String.format(BASE_URL, "/Search/Advanced"), HttpMethod.GET).cookieStore(cookies).withProxy(proxy), this.crawlerConfig.getMaxRetryAttempts());

    for (String search : searchList) {

      var payload = String.format("{\"isAdvancedSearch\":true,\"displayLanguage\":\"EN\",\"CountryCodes\":[],\"Coordinates\":{\"Latitude\":\"%s\",\"Longitude\":\"%s\",\"Distance\":\"5000\"},\"Categories\":[],\"BusinessTypes\":[\"0\"],\"ImportExportAgentCodes\":[],\"ContactTypes\":[]}", search.split("/")[0], search.split("/")[1]);

      //var payloadTeste = "{\"isAdvancedSearch\":true,\"displayLanguage\":\"EN\",\"CountryCodes\":[],\"Categories\":[],\"BusinessTypes\":[],\"ImportExportAgentCodes\":[],\"ContactTypes\":[],\"BusinessName\":\"itau\"}";
      response = this.doHttpRequest(new HttpGatewayImpl(String.format(BASE_URL, "/Search/DoSearch"), HttpMethod.POST)
        .connectionTimeout(this.crawlerConfig.getConnectionTimeout())
        .socketTimeout(this.crawlerConfig.getSocketTimeout())
        .headers(this.generateHeadersSearchAction())
        .mediaType(MediaType.APPLICATION_JSON)
        .bodyString(payload)
        .timeToSleepBetweenRequests(0)
        .cookieStore(cookies).withProxy(proxy), this.crawlerConfig.getMaxRetryAttempts());
      boolean loop = true;
      while (loop) {
        var pageLink = this.jsonPath(response.getContent(), "$.PaginationList.NextLink");
        if (pageLink == null) {
          loop = false;
          continue;
        }

        final List<Map<String, Object>> companys = this.jsonPath(response.getContent(), "$.Results.Records");

        for (final Map<String, Object> company : companys) {
          this.outputMessageBroker.sendMessage(m
            .withPayload(this.gson.toJson(company))
            .withRequestStatus(RequestStatus.FOUND)
            .withParameters(this.buildMessageParams(m))
            .withMessageDate(LocalDateTime.now()));
        }
        if (pageLink != null) {
          var page = pageLink.toString().split("&page=")[1];
          var pagePayload = String.format("{\"displayLanguage\":\"EN\",\"page\":%d}", Integer.parseInt(page));
          response = this.doHttpRequest(new HttpGatewayImpl(String.format(BASE_URL, pageLink.toString()), HttpMethod.GET).cookieStore(cookies).withProxy(proxy).headers(this.generateHeadersSearchAction()), this.crawlerConfig.getMaxRetryAttempts());

          response = this.doHttpRequest(new HttpGatewayImpl(String.format(BASE_URL, "/Search/GoToPage"), HttpMethod.POST)
            .connectionTimeout(this.crawlerConfig.getConnectionTimeout())
            .socketTimeout(this.crawlerConfig.getSocketTimeout())
            .headers(this.generateHeadersSearchAction())
            .mediaType(MediaType.APPLICATION_JSON)
            .bodyString(pagePayload)
            .timeToSleepBetweenRequests(0)
            .cookieStore(cookies).withProxy(proxy), this.crawlerConfig.getMaxRetryAttempts());
        }
      }
    }
    return Optional.empty();
  }


  private Proxy retrieveProxy() {
    final Proxy proxy = this.proxyRepository.get(this.thread()).orElse(null);
    if (proxy != null)
      return proxy;

    final var pR = this.neowayService.retrieveProxy();
    if (pR.isPresent()) {
      final var neowayProxy = pR.get().getProxy();
      final var auth = neowayProxy.getAuth();
      final var proxyNew = new Proxy(auth.getUser(), auth.getPassword(), neowayProxy.getIp(),
        Integer.parseInt(neowayProxy.getPort()));
      this.proxyRepository.put(this.thread(), proxyNew);
      return proxyNew;
    }
    return null;
  }

  private void buildSearchList(final List<String> searchList) throws IOException {
    FileInputStream fstreamterms = new FileInputStream(SEARCH_PATH);
    BufferedReader bR = new BufferedReader(new InputStreamReader(fstreamterms));
    String params;
    while ((params = bR.readLine()) != null) {
      searchList.add(params);
    }
    bR.close();
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
    params.put("__RequestVerificationToken", token);
    params.put("email", "go@equify.com.br");
    params.put("password", "Mrvgbz@17");
    return params;
  }

  private Map<String, String> generateHeadersLoginAction() {
    final Map<String, String> headers = new HashMap<>(0);
    headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
    headers.put("Accept-Encoding", "gzip, deflate, br");
    headers.put("Accept-Language", "pt-BR,pt;q=0.8,en-US;q=0.5,en;q=0.3");
    headers.put("Connection", "keep-alive");
    headers.put("Content-Type", "application/x-www-form-urlencoded");
    headers.put("Host", "use.infobelpro.com");
    headers.put("Upgrade-Insecure-Requests", "1");
    headers.put("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:95.0) Gecko/20100101 Firefox/95.0");
    return headers;
  }

  private Map<String, String> generateHeadersSearchAction() {
    final Map<String, String> headers = new HashMap<>(0);
    headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
    headers.put("Accept-Encoding", "gzip, deflate, br");
    headers.put("Accept-Language", "pt-BR,pt;q=0.8,en-US;q=0.5,en;q=0.3");
    headers.put("Connection", "keep-alive");
    headers.put("Content-Type", "application/json");
    headers.put("Host", "use.infobelpro.com");
    headers.put("Origin", "https://use.infobelpro.com");
    headers.put("Referer", "https://use.infobelpro.com/Search/Advanced");
    headers.put("Upgrade-Insecure-Requests", "1");
    headers.put("X-Requested-With", "XMLHttpRequest");
    headers.put("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:95.0) Gecko/20100101 Firefox/95.0");
    return headers;
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
