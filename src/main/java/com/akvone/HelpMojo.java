package com.akvone;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "help")
public class HelpMojo extends AbstractMojo {

  private static final String FULL_COMMAND = "mvn clean package -DskipTests com.akvone:update-image-maven-plugin:update";
  private static final String REDUCED_COMMAND = "mvn com.akvone:update-image-maven-plugin:update";

  @Override
  public void execute() {
    getLog().info("To make it work do next steps:");
    getLog().info("- Run docker and enable 'Docker without TLS' (Docker -> Settings -> General -> 'Expose daemon on tcp://localhost:2375 without tls')");
    getLog().info("- Add required properties in {root}/dev/properties.yaml");
    getLog().info("---");
    getLog().info("Type: " + FULL_COMMAND + " to make full update");
    getLog().info("Type: " + REDUCED_COMMAND + " To make partial update (without generating new jar)");
  }
}
