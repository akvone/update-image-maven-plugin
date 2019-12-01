package com.github.akvone;

import com.github.akvone.core.AbstractMojoForRoot;
import com.github.akvone.core.ImageUpdater;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "build-push")
public class BuildAndPushMojo extends AbstractMojoForRoot {

  public void executeOnRoot() {
    ImageUpdater imageUpdater = new ImageUpdater(getLog(), project, false);
    imageUpdater.execute();
  }
}
