package com.github.akvone.core;

import com.github.akvone.properties.OpenShiftProperties;
import com.github.akvone.properties.PropertiesHolder;
import com.github.akvone.services.DockerBuilder;
import com.github.akvone.services.OpenShiftPatcher;
import com.github.akvone.services.YamlPropsService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.project.MavenProject;

@Slf4j
public class ImageUpdater {

  private final MavenProject project;
  private final String projectArtifactId;
  private final boolean alsoMakeUpdateInCloud;

  private Runnable preExecuteCallback = () -> {};

  public ImageUpdater(MavenProject project, boolean alsoMakeUpdateInCloud) {
    this.project = project;
    this.projectArtifactId = project.getArtifactId();
    this.alsoMakeUpdateInCloud = alsoMakeUpdateInCloud;
  }

  public void setPreExecuteCallback(Runnable callback) {
    this.preExecuteCallback = callback;
  }

  public void execute() {
    preExecuteCallback.run();
    PropertiesHolder propsHolder = new YamlPropsService().createPropertiesHolder();

    String imageLocation = uploadDockerImage(propsHolder);

    if (alsoMakeUpdateInCloud) {
      patchOpenShiftDeployment(propsHolder, imageLocation);
    }
  }

  private void patchOpenShiftDeployment(PropertiesHolder propsHolder, String imageLocation) {
    log.info("Start to patch OpenShift deployment with an image: {}", imageLocation);
    OpenShiftPatcher openShiftPatcher = new OpenShiftPatcher(generateOpenShiftProperties(propsHolder));
    openShiftPatcher.patchOpenShiftDeployment(imageLocation);
  }

  private String uploadDockerImage(PropertiesHolder propsHolder) {
    log.info("Starting to connect to docker");
    DockerBuilder dockerBuilder = new DockerBuilder(propsHolder, projectArtifactId);
    log.info("Connected to docker");

    String imagePath = null;
    try {
      log.info("Starting building and pushing image");
      imagePath = dockerBuilder.run();
      log.info("Push is successful. Image location: {}", imagePath);
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
