package org.folio.okapi.common;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;

public class HttpClientCached {
  HttpClient httpClient;
  
  HttpClientCached(HttpClient httpClient) {
    this.httpClient = httpClient;
  }
    
  HttpClientRequest requestAbs(HttpMethod method, String url, Handler<AsyncResult<HttpClientResponse>> hndlr) {
    HttpClientRequest req = httpClient.requestAbs(method, url, hndlr);
    return new HttpClientRequestCached(req);    
  }
}
