package bot.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Named;
import javax.inject.Singleton;

import http.domain.Proxy;

@Named
@Singleton
public class NetworkProxyRepository implements ProxyRepository {

  private final Map<String, Proxy> proxies;

  public NetworkProxyRepository() {
    this.proxies = new HashMap<>(0);
  }

  @Override
  public void put(final String threadNameId, final Proxy proxy) {
    this.proxies.put(threadNameId, proxy);
  }

  @Override
  public Optional<Proxy> get(final String threadNameId) {
    return Optional.ofNullable(this.proxies.get(threadNameId));
  }

  @Override
  public void remove(final String threadNameId) {
    this.proxies.remove(threadNameId);
  }
}
