package com.github.akvone;

import com.github.akvone.core.AbstractMojoForRoot;
import com.github.akvone.core.ImageUpdater;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "update")
public class UpdateMojo extends AbstractMojoForRoot {

  @Override
  public void executeOnRoot() {
    ImageUpdater imageUpdater = new ImageUpdater(getLog(), project, true);
    imageUpdater.execute();
  }

}