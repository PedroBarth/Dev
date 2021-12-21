package bot.repository;

import java.util.Optional;

import http.domain.Proxy;

public interface ProxyRepository {

  void put(final String threadNameId, final Proxy proxy);

  Optional<Proxy> get(final String threadNameId);

  void remove(final String threadNameId);

}
