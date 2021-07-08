package com.kaszuba.eipa.EIPAIngestionApp.config;

import com.kaszuba.eipa.EIPAIngestionApp.OrchestratorVerticle;
import com.kaszuba.eipa.EIPAIngestionApp.repository.CacheVerticle;
import com.kaszuba.eipa.EIPAIngestionApp.web.HttpVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;

public class ConfigurationVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    ConfigStoreOptions fileStore = new ConfigStoreOptions()
      .setType("file")
      .setOptional(true)
      .setConfig(new JsonObject().put("path", "conf/config.json"));
    ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(fileStore);
    ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
    retriever.getConfig(ar -> {
      if (ar.failed()) {
        throw new RuntimeException("Could not retrieve configuration. Closing app...");
      } else {
        vertx.deployVerticle(HttpVerticle.class.getName(), new DeploymentOptions().setConfig(ar.result()));
        vertx.deployVerticle(CacheVerticle.class.getName());
        vertx.deployVerticle(OrchestratorVerticle.class.getName(), new DeploymentOptions().setConfig(ar.result()));
      }
    });
  }
}
