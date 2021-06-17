package org.gs.health;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import java.util.Optional;

@Readiness
public class TvSeriesCheck implements HealthCheck {

  @Override
  public HealthCheckResponse call() {

    Optional<String> defaultTitle =
        ConfigProvider.getConfig().getOptionalValue("default.title", String.class);
    if (defaultTitle.isPresent()) {
      return HealthCheckResponse.named("TvSeries Check").up().build();
    } else {
      return HealthCheckResponse.named("TvSeries Check").down().build();
    }
  }
}
