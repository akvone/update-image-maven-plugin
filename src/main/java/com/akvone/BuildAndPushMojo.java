package com.akvone;

import com.akvone.core.AbstractMojoForRoot;
import com.akvone.core.ImageUpdater;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "build-push")
public class BuildAndPushMojo extends AbstractMojoForRoot {

  public void executeOnRoot() {
    ImageUpdater imageUpdater = new ImageUpdater(getLog(), project, false);
    imageUpdater.execute();
  }
}
