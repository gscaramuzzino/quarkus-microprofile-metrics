package org.gs;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
  public Response getAll() {
    List<TvSeriesEntity> tvSeriesEntities = repository.listAll();
    return Response.ok(tvSeriesEntities).build();
  }
}
