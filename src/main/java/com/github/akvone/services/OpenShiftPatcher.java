package com.github.akvone.services;

import static java.lang.String.format;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.akvone.properties.OpenShiftProperties;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

@Slf4j
public class OpenShiftPatcher {

  private static final String SUCCESSFUL_RESULT_URL = "%s/console/project/%s/browse/dc/%s";
  private static final String EDIT_DEPLOYMENT_MANUALLY_PATTERN = "%s/console/project/%s/edit/dc/%s";
  private static final String OPENSHIFT_DEPLOYMENT_URL_PATTERN = "%s/oapi/v1/namespaces/%s/deploymentconfigs/%s";

  private final OpenShiftProperties props;
  private final String patchFullUrl;
  private final String editDeploymentManuallyUrl;
  private final String successfulResultUrl;

  public OpenShiftPatcher(OpenShiftProperties props) {
    this.props = props;
    patchFullUrl = format(OPENSHIFT_DEPLOYMENT_URL_PATTERN, props.serverUrl, props.namespace, props.appName);
    editDeploymentManuallyUrl = format(EDIT_DEPLOYMENT_MANUALLY_PATTERN, props.serverUrl, props.namespace, props.appName);
    successfulResultUrl = format(SUCCESSFUL_RESULT_URL, props.serverUrl, props.namespace, props.appName);
  }

  public void patchOpenShiftDeployment(String newImagePath) {

    try (CloseableHttpClient httpclient = HttpClients
        .custom()
        .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())// TODO:
        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE) // TODO:
        .build()) {
      HttpPatch httpPatch = buildHttpPatch(newImagePath);

      CloseableHttpResponse response = httpclient.execute(httpPatch);
      int statusCode = response.getStatusLine().getStatusCode();
      log.info("Status code {}", statusCode);
      if (statusCode >= 400) {
        throw new IllegalStateException("TODO"); // TODO: throw another suitable exception
      }
      log.info("See result here: {}", successfulResultUrl);
    } catch (Exception e) {
      log.warn("Tried to update but has no success. Please update it manually:");
      log.warn("Image location: {}", newImagePath);
      log.warn("Update here: {}", editDeploymentManuallyUrl);
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

}
