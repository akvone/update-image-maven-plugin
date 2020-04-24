package com.github.akvone.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Slf4j
public abstract class AbstractMojoForRoot extends AbstractMojo {

  @Parameter(defaultValue = "${project}", readonly = true)
  protected MavenProject project;

  public abstract void executeOnRoot();

  @Override
  public void execute() {
    if (isRoot()) {
      executeOnRoot();
    } else {
      log.info("Not a root module. Skip");
    }
  }

  private boolean isRoot() {
    return project.isExecutionRoot();
  }

}
