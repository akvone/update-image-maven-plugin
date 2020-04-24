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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DockerBuilder {

  private static final String TAG_DATE_TIME_PATTERN = "YYYYMMddHHmmSS";

  private String fullRepositoryUrl;

  private final String projectName;

  private final DockerClient docker;
  private final PropertiesHolder propsHolder;

  public DockerBuilder(PropertiesHolder propsHolder, String projectName) {
    this.projectName = projectName;
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
    String tag = tagImage(imageId);
    String imagePath = pushImage(tag);

    docker.close();
    return imagePath;
  }

  private String pushImage(String tag) throws InterruptedException {
    PushImageResultCallback pc = new PushImageResultCallback() {
      @Override
      public void onNext(PushResponseItem item) {
        log.info(item.toString());
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

  private String tagImage(String imageId) {
    String tag = LocalDateTime.now().format(DateTimeFormatter.ofPattern(TAG_DATE_TIME_PATTERN));
    docker.tagImageCmd(imageId, fullRepositoryUrl + projectName, tag).exec();

    return tag;
  }

  private String buildImage() {
    BuildImageResultCallback callback = new BuildImageResultCallback() {
      @Override
      public void onNext(BuildResponseItem item) {
        log.info(item.toString());
        super.onNext(item);
      }
    };
    return docker.buildImageCmd(new File("."))
        .exec(callback)
        .awaitImageId();
  }
}
