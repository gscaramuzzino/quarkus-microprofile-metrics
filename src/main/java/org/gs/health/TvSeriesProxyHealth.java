package org.gs.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.gs.TvSeries;
import org.gs.TvSeriesProxy;

import javax.inject.Inject;

@Liveness
public class TvSeriesProxyHealth implements HealthCheck {

  @Inject @RestClient TvSeriesProxy proxy;

  @Override
  public HealthCheckResponse call() {
    proxy.get("title");
    return HealthCheckResponse.named("TvMaze APIs").up().build();
  }

}
