package com.github.akvone.properties;

import static com.github.akvone.properties.PropPair.of;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class PropertiesHolderTest {

  @Test
  public void complexTest() {
    List<PropPair> configs = Arrays.asList(
        of(fullFileName("user.yaml"), false),
        of("non existing file", false),
        of("config/OS/Linux-default.yaml", true),
        of("config/default.yaml", true)
    );
    PropertiesHolder ph = PropertiesHolder.create(configs);

    assertEquals("unix:///var/run/docker.sock", ph.get("docker", "host"));
    assertEquals("openshift", ph.get("cloudProvider", "type"));
  }

  @Test
  public void priorityTest(){
    List<PropPair> configs = Arrays.asList(
        of(fullFileName("3.yaml"), true),
        of(fullFileName("2.yaml"), true),
        of(fullFileName("1.yaml"), true)
    );
    PropertiesHolder ph = PropertiesHolder.create(configs);

    assertEquals("com", ph.get("build", "package"));
    assertEquals("mac", ph.get("build", "os"));
    assertEquals("1", ph.get("build", "version"));
  }

  @Test(expected = IllegalStateException.class)
  public void configNotExistsTest(){
    List<PropPair> configs = Arrays.asList(
        of(fullFileName("user.yaml"), false),
        of("non existing file", true)
    );
    PropertiesHolder ignored = PropertiesHolder.create(configs);
  }

  private String fullFileName(String fileName) {
    return "config/PropertiesHolderTest/" + fileName;
  }
}