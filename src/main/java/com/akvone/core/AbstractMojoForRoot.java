package com.akvone.core;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

public abstract class AbstractMojoForRoot extends AbstractMojo {

  @Parameter(defaultValue = "${project}", readonly = true)
  protected MavenProject project;

  public abstract void executeOnRoot();

  @Override
  public void execute() {
    if (isRoot()) {
      executeOnRoot();
    } else {
      getLog().info("Not a root module. Skip");
    }
  }

  private boolean isRoot() {
    return project.isExecutionRoot();
  }

}
