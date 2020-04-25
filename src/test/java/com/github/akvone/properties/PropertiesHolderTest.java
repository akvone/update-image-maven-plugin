package com.github.akvone.properties;

import static com.github.akvone.properties.PropertyPair.of;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PropertiesHolderTest {

  @Test
  public void complexTest() {
    List<PropertyPair> configs = Arrays.asList(
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
    List<PropertyPair> configs = Arrays.asList(
        of(fullFileName("3.yaml"), true),
        of(fullFileName("2.yaml"), true),
        of(fullFileName("1.yaml"), true)
    );
    PropertiesHolder ph = PropertiesHolder.create(configs);

    assertEquals("com", ph.get("build", "package"));
    assertEquals("mac", ph.get("build", "os"));
    assertEquals("1", ph.get("build", "version"));
  }

  @Test
  public void configNotExistsTest(){
    assertThrows(IllegalStateException.class, () -> {
      List<PropertyPair> configs = Arrays.asList(
          of(fullFileName("user.yaml"), false),
          of("non existing file", true)
      );
      PropertiesHolder ignored = PropertiesHolder.create(configs);
    });
  }

  private String fullFileName(String fileName) {
    return "config/PropertiesHolderTest/" + fileName;
  }
}