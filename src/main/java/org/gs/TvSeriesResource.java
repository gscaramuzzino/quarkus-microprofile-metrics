package org.gs;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/tvseries")
public class TvSeriesResource {

  String defaultTitle;

  @Inject @RestClient TvSeriesProxy proxy;

  @Inject TvSeriesRepository repository;

  @PostConstruct
  void init() {
    ConfigProvider.getConfig()
        .getOptionalValue("default.title", String.class)
        .ifPresent(title -> defaultTitle = title);
  }

  @GET
  @Path("/fetch")
  @Transactional
  @Produces(MediaType.APPLICATION_JSON)
  @Counted(
      name = "countFetchTvSeries",
      description = "Count how many times the fetchTvSeries has been invoked")
  @Timed(
      name = "timeFetchTvSeries",
      description = "How long it takes to invoke the fetchTvSeries",
      unit = MetricUnits.MILLISECONDS)
  @Metered(
      name = "meteredFetchTvSeries",
      description = "Measures throughput of fetchTvSeries method")
  public Response fetchTvSeries(@QueryParam("title") String title) {

    if (title == null) {
      title = defaultTitle;
    }
    TvSeries tvSeries = proxy.get(title);

    TvSeriesEntity tvSeriesEntity = new TvSeriesEntity();
    tvSeriesEntity.setName(tvSeries.getName());
    tvSeriesEntity.setSummary(tvSeries.getSummary());

    repository.persist(tvSeriesEntity);
    if (repository.isPersistent(tvSeriesEntity)) {
      return Response.ok(tvSeriesEntity).build();
    }
    return Response.status(Response.Status.BAD_REQUEST).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Counted(name = "countGetAll", description = "Count how many times the getAll has been invoked")
  @Timed(
      name = "timeGetAll",
      description = "How long it takes to invoke the getAll",
      unit = MetricUnits.MILLISECONDS)
  @Metered(name = "meteredGetAll", description = "Measures throughput of getAll method")
  public Response getAll() {
    List<TvSeriesEntity> tvSeriesEntities = repository.listAll();
    return Response.ok(tvSeriesEntities).build();
  }
}
