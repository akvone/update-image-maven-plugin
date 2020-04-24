package com.github.akvone.properties;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PropertiesHolderTest {

  @Test
  public void test() {
    PropertiesHolder propertiesHolder
        = PropertiesHolder.create("config/default.yaml", "config/OS/Linux-default.yaml","non existing file", "config/user.yaml");

    assertEquals("openshift", propertiesHolder.get("cloudProvider", "type"));
  }
}