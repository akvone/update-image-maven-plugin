package com.github.akvone.services;

import com.github.akvone.properties.PropertiesHolder;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.github.dockerjava.okhttp.OkHttpDockerCmdExecFactory;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.maven.plugin.logging.Log;

public class DockerBuilder {

  private static final String TAG_DATE_TIME_PATTERN = "YYYYMMddHHmmSS";

  private String fullRepositoryUrl;

  private final Log log;
  private final String projectName;
  private final String tag;

  private final DockerClient docker;
  private final PropertiesHolder propsHolder;

  public DockerBuilder(PropertiesHolder propsHolder, Log log, String projectName) {
    this.log = log;
    this.projectName = projectName;
    this.tag = LocalDateTime.now().format(DateTimeFormatter.ofPattern(TAG_DATE_TIME_PATTERN));
    this.propsHolder = propsHolder;
    fullRepositoryUrl = getArtifactoryProp("url") + getArtifactoryProp("repository");

    DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
        .withDockerHost(getDockerProp("host"))
        .withRegistryUrl(fullRepositoryUrl)
        .withRegistryUsername(getDockerProp("username"))
        .withRegistryPassword(getDockerProp("authorization"))
        .build();
    this.docker = DockerClientBuilder.getInstance(config)
        .withDockerCmdExecFactory(new OkHttpDockerCmdExecFactory())
        .build();
  }

  private String getArtifactoryProp(String key) {
    return propsHolder.get("artifactory", key);
  }

  private String getDockerProp(String key) {
    return propsHolder.get("docker", key);
  }

  public String run() throws IOException, InterruptedException {
    String imageId = buildImage();
    tagImage(imageId);
    String imagePath = pushImage();

    docker.close();
    return imagePath;
  }

  private String pushImage() throws InterruptedException {
    PushImageResultCallback pc = new PushImageResultCallback() {
      @Override
      public void onNext(PushResponseItem item) {
        System.out.println(item);
        super.onNext(item);
      }
    };
    String imagePath = fullRepositoryUrl + projectName;
    docker.pushImageCmd(imagePath)
        .withTag(tag)
        .exec(pc)
        .awaitCompletion();

    return imagePath + ":" + tag;
  }

  private void tagImage(String imageId) {
    docker.tagImageCmd(imageId, fullRepositoryUrl + projectName, tag).exec();
  }

  private String buildImage() {
    BuildImageResultCallback callback = new BuildImageResultCallback() {
      @Override
      public void onNext(BuildResponseItem item) {
        System.out.println(item);
        super.onNext(item);
      }
    };
    return docker.buildImageCmd(new File("."))
        .exec(callback)
        .awaitImageId();
  }
}
