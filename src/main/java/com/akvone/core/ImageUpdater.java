package com.akvone.core;

import com.akvone.properties.OpenShiftProperties;
import com.akvone.properties.PropertiesHolder;
import com.akvone.services.DockerBuilder;
import com.akvone.services.OpenShiftPatcher;
import java.io.IOException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class ImageUpdater {

  private static final String DEFAULT_YAML_FILE_LOCATION = "config/default.yaml";
  private static final String USER_YAML_FILE_LOCATION = "gitignore/properties.yaml";

  private final MavenProject project;
  private final Log log;

  private Runnable preExecuteCallback = () -> {};

  public ImageUpdater(Log log, MavenProject project) {
    this.log = log;
    this.project = project;
  }

  public void setPreExecuteCallback(Runnable callback) {
    this.preExecuteCallback = callback;
  }

  public void execute() {
    preExecuteCallback.run();
    PropertiesHolder propsHolder = PropertiesHolder.create(DEFAULT_YAML_FILE_LOCATION, USER_YAML_FILE_LOCATION);

    String imageLocation = uploadDockerImage(propsHolder);
    patchOpenshiftDeployment(propsHolder, imageLocation);
  }

  private void patchOpenshiftDeployment(PropertiesHolder propsHolder, String imageLocation) {
    log.info("Start to push image: " + imageLocation);
    new OpenShiftPatcher(generateOpenShiftPropertiesProperties(propsHolder)).patchOpenShiftDeployment(imageLocation);
  }

  private String uploadDockerImage(PropertiesHolder propsHolder) {
    log.info("Starting to connect to docker");
    DockerBuilder dockerBuilder = new DockerBuilder(propsHolder, log, project.getName());
    log.info("Connected to docker");

    String imagePath = null;
    try {
      log.info("Starting building and pushing image");
      imagePath = dockerBuilder.run();
      log.info("Push is successful. Image location: " + imagePath);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }

    return imagePath;
  }

  private OpenShiftProperties generateOpenShiftPropertiesProperties(PropertiesHolder propsHolder) {
    OpenShiftProperties props = new OpenShiftProperties();
    props.serverUrl = propsHolder.get("cloudProvider", "url");
    props.namespace = propsHolder.get("cloudProvider", "namespace");
    props.authorization = propsHolder.get("cloudProvider", "authorizationToken");

    props.appName = project.getName();

    return props;
  }

}
