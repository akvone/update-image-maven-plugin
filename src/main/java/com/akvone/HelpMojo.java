package com.akvone;

import com.akvone.core.AbstractMojoForRoot;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "help")
public class HelpMojo extends AbstractMojoForRoot {

  private static final String FULL_COMMAND = "mvn clean package -DskipTests com.akvone:update-image-maven-plugin:update";
  private static final String REDUCED_UPDATE_COMMAND = "mvn com.akvone:update-image-maven-plugin:update";
  private static final String REDUCED_BUILD_PUSH_COMMAND = "mvn com.akvone:update-image-maven-plugin:build-push";

  @Override
  public void executeOnRoot() {
    getLog().info("To make it work do next steps:");
    getLog().info("- Run docker and enable 'Docker without TLS' (Docker -> Settings -> General -> 'Expose daemon on tcp://localhost:2375 without tls')");
    getLog().info("- Add required properties in '{root}/../config.yaml' or '{root}/gitignore/config.yaml'");
    getLog().info("---");
    getLog().info("Type: " + FULL_COMMAND + " to make full update");
    getLog().info("Type: " + REDUCED_UPDATE_COMMAND + " To make update only (without generating new jar)");
    getLog().info("Type: " + REDUCED_BUILD_PUSH_COMMAND + " To make build and push only (without generating new jar)");
  }
}
