package com.akvone.services;

import com.akvone.properties.OpenShiftProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

public class OpenShiftPatcher {

  private static final String OPENSHIFT_DEPLOYMENT_URL_PATTERN = "%s/oapi/v1/namespaces/%s/deploymentconfigs/%s";
  private static final String OPENSHIFT_PODS_BY_NAMESPACE_URL = "/api/v1/namespaces/%s/pods";

  private final OpenShiftProperties props;
  private final String patchFullUrl;

  public OpenShiftPatcher(OpenShiftProperties props) {
    this.props = props;
    patchFullUrl = String.format(OPENSHIFT_DEPLOYMENT_URL_PATTERN, props.serverUrl, props.namespace, props.appName);
  }

  public void patchOpenShiftDeployment(String newImagePath) {

    try (CloseableHttpClient httpclient = HttpClients
        .custom()
        .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())// TODO:
        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE) // TODO:
        .build()) {
      HttpPatch httpPatch = buildHttpPatch(newImagePath);

      CloseableHttpResponse response = httpclient.execute(httpPatch);
      System.out.println("Status code " + response.getStatusLine().getStatusCode());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private HttpPatch buildHttpPatch(String newImagePath)
      throws JsonProcessingException {

    HttpPatch httpPatch = new HttpPatch(patchFullUrl);
    HttpEntity httpEntity = new StringEntity(prepareJson(newImagePath), ContentType.APPLICATION_JSON);
    httpPatch.setEntity(httpEntity);
    httpPatch.setHeader("Content-Type", "application/json-patch+json");
    httpPatch.setHeader("Authorization", props.authorization);

    return httpPatch;
  }

  private String prepareJson(String newImagePath) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    Object object = ImmutableList.of(
        ImmutableMap.of(
            "op", "replace",
            "path", "/spec/template/spec/containers/0/image",
            "value", newImagePath)
    );

    return objectMapper.writeValueAsString(object);
  }

  private String getLatestPod(String appName, OpenShiftProperties props) {
    String url = String.format(OPENSHIFT_PODS_BY_NAMESPACE_URL, props.namespace);
    HttpGet httpGet = new HttpGet(url);
    //TODO

    return null;
  }
}
