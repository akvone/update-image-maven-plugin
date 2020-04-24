package com.github.akvone;

import com.github.akvone.core.AbstractMojoForRoot;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugins.annotations.Mojo;

@Slf4j
@Mojo(name = "help")
public class HelpMojo extends AbstractMojoForRoot {

  private static final String FULL_COMMAND = "mvn clean package -DskipTests com.github.akvone:update-image-maven-plugin:update";
  private static final String REDUCED_UPDATE_COMMAND = "mvn com.github.akvone:update-image-maven-plugin:update";
  private static final String REDUCED_BUILD_PUSH_COMMAND = "mvn com.github.akvone:update-image-maven-plugin:build-push";

  @Override
  public void executeOnRoot() {
    log.info("To make it work do next steps:");
    log.info("- Run docker and enable 'Docker without TLS' (Docker -> Settings -> General -> 'Expose daemon on tcp://localhost:2375 without tls')");
    log.info("- Add required properties in '{root}/../config.yaml' or '{root}/gitignore/config.yaml'");
    log.info("---");
    log.info("Type: " + FULL_COMMAND + " to make full update");
    log.info("Type: " + REDUCED_UPDATE_COMMAND + " To make update only (without generating new jar)");
    log.info("Type: " + REDUCED_BUILD_PUSH_COMMAND + " To make build and push only (without generating new jar)");
  }
}
