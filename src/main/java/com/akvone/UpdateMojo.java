package com.akvone;

import com.akvone.core.ImageUpdater;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "update")
public class UpdateMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  public void execute() {
    ImageUpdater imageUpdater = new ImageUpdater(getLog(), project);
    imageUpdater.execute();
  }

}