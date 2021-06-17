package org.gs.health;

import io.smallrye.health.checks.UrlHealthCheck;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TvSeriesProxyUrl {

  @Liveness
  HealthCheck url() {
    return new UrlHealthCheck("https://api.tvmaze.com/shows/1/episodes").name("API check url");
  }
}
