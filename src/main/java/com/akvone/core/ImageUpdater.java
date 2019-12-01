package com.akvone.core;

import com.akvone.services.DockerBuilder;
import com.akvone.services.OpenShiftPatcher;
import com.akvone.properties.OpenShiftProperties;
import com.akvone.properties.PropertiesHolder;
import java.io.IOException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

public class ImageUpdater {

  private static final String DEFAULT_YAML_FILE_LOCATION = "config/default.yaml";
  private static final String USER_YAML_FILE_LOCATION_ROOT = "../config.yaml";
  private static final String USER_YAML_FILE_LOCATION_SPECIFIC = "gitignore/config.yaml";

  private final MavenProject project;
  private final String projectArtifactId;
  private final Log log;
  private final boolean alsoMakeUpdateInCloud;

  private Runnable preExecuteCallback = () -> {};

  public ImageUpdater(Log log, MavenProject project, boolean alsoMakeUpdateInCloud) {
    this.log = log;
    this.project = project;
    this.projectArtifactId = project.getArtifactId();
    this.alsoMakeUpdateInCloud = alsoMakeUpdateInCloud;
  }

  public void setPreExecuteCallback(Runnable callback) {
    this.preExecuteCallback = callback;
  }

  public void execute() {
    preExecuteCallback.run();
    PropertiesHolder propsHolder = PropertiesHolder.create(
        DEFAULT_YAML_FILE_LOCATION,
        USER_YAML_FILE_LOCATION_ROOT,
        USER_YAML_FILE_LOCATION_SPECIFIC);

    String imageLocation = uploadDockerImage(propsHolder);

    if (alsoMakeUpdateInCloud) {
      patchOpenshiftDeployment(propsHolder, imageLocation);
    }
  }

  private void patchOpenshiftDeployment(PropertiesHolder propsHolder, String imageLocation) {
    log.info("Start to patch openshift deployment with an image: " + imageLocation);
    OpenShiftPatcher openShiftPatcher = new OpenShiftPatcher(log, generateOpenShiftProperties(propsHolder));
    openShiftPatcher.patchOpenShiftDeployment(imageLocation);
  }

  private String uploadDockerImage(PropertiesHolder propsHolder) {
    log.info("Starting to connect to docker");
    DockerBuilder dockerBuilder = new DockerBuilder(propsHolder, log, projectArtifactId);
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

  private OpenShiftProperties generateOpenShiftProperties(PropertiesHolder propsHolder) {
    OpenShiftProperties props = new OpenShiftProperties();
    props.serverUrl = propsHolder.get("cloudProvider", "url");
    props.namespace = propsHolder.get("cloudProvider", "namespace");
    props.authorization = propsHolder.get("cloudProvider", "authorizationToken");

    props.appName = projectArtifactId;

    return props;
  }

}
