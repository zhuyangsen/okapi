package org.folio.okapi.common;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpConnection;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.http.StreamPriority;

public class HttpClientRequestCached implements HttpClientRequest {
  HttpClientRequest httpClientRequest;
  
  HttpClientRequestCached(HttpClientRequest httpClientRequest) {
    this.httpClientRequest = httpClientRequest;
  }
  
  @Override
  public HttpClientRequest exceptionHandler(Handler<Throwable> hndlr) {
    httpClientRequest.exceptionHandler(hndlr);
    return this;
  }

  @Override
  public HttpClientRequest setWriteQueueMaxSize(int i) {
    httpClientRequest.setWriteQueueMaxSize(i);
    return this;
  }

  @Override
  public HttpClientRequest drainHandler(Handler<Void> hndlr) {
    httpClientRequest.drainHandler(hndlr);
    return this;
  }

  @Override
  public HttpClientRequest setFollowRedirects(boolean bln) {
    httpClientRequest.setFollowRedirects(bln);
    return this;
  }

  @Override
  public HttpClientRequest setMaxRedirects(int i) {
    httpClientRequest.setMaxRedirects(i);
    return this;
  }

  @Override
  public HttpClientRequest setChunked(boolean bln) {
    httpClientRequest.setChunked(bln);
    return this;
  }

  @Override
  public boolean isChunked() {
    return httpClientRequest.isChunked();
  }

  @Override
  public HttpMethod method() {
    return httpClientRequest.method();
  }

  @Override
  public String getRawMethod() {
    return httpClientRequest.getRawMethod();
  }

  @Override
  public HttpClientRequest setRawMethod(String string) {
    httpClientRequest.setRawMethod(string);
    return this;
  }

  @Override
  public String absoluteURI() {
    return httpClientRequest.absoluteURI();
  }

  @Override
  public String uri() {
    return httpClientRequest.uri();
  }

  @Override
  public String path() {
    return httpClientRequest.path();
  }

  @Override
  public String query() {
    return httpClientRequest.query();
  }

  @Override
  public HttpClientRequest setHost(String string) {
    httpClientRequest.setHost(string);
    return this;
  }

  @Override
  public String getHost() {
    return httpClientRequest.getHost();
  }

  @Override
  public MultiMap headers() {
    return httpClientRequest.headers();
  }

  @Override
  public HttpClientRequest putHeader(String string, String string1) {
    httpClientRequest.putHeader(string, string1);
    return this;
  }

  @Override
  public HttpClientRequest putHeader(CharSequence cs, CharSequence cs1) {
    httpClientRequest.putHeader(cs, cs1);
    return this;
  }

  @Override
  public HttpClientRequest putHeader(String string, Iterable<String> itrbl) {
    httpClientRequest.putHeader(string, itrbl);
    return this;
  }

  @Override
  public HttpClientRequest putHeader(CharSequence cs, Iterable<CharSequence> itrbl) {
    httpClientRequest.putHeader(cs, itrbl);
    return this;
  }

  @Override
  public Future<Void> write(String string) {
    return httpClientRequest.write(string);
  }

  @Override
  public void write(String string, Handler<AsyncResult<Void>> hndlr) {
    httpClientRequest.write(string, hndlr);
  }

  @Override
  public Future<Void> write(String string, String string1) {
    return httpClientRequest.write(string, string1);
  }

  @Override
  public void write(String string, String string1, Handler<AsyncResult<Void>> hndlr) {
    httpClientRequest.write(string, string1, hndlr);
  }

  @Override
  public HttpClientRequest continueHandler(Handler<Void> hndlr) {
    httpClientRequest.continueHandler(hndlr);
    return this;
  }

  @Override
  public Future<HttpVersion> sendHead() {
    return httpClientRequest.sendHead();
  }

  @Override
  public HttpClientRequest sendHead(Handler<AsyncResult<HttpVersion>> hndlr) {
    httpClientRequest.sendHead(hndlr);
    return this;
  }

  @Override
  public Future<Void> end(String string) {
    return httpClientRequest.end(string);
  }

  @Override
  public void end(String string, Handler<AsyncResult<Void>> hndlr) {
    httpClientRequest.end(string, hndlr);
  }

  @Override
  public Future<Void> end(String string, String string1) {
    return httpClientRequest.end(string, string1);
  }

  @Override
  public void end(String string, String string1, Handler<AsyncResult<Void>> hndlr) {
    httpClientRequest.end(string, string1, hndlr);
  }

  @Override
  public Future<Void> end(Buffer buffer) {
    return httpClientRequest.end(buffer);
  }

  @Override
  public void end(Buffer buffer, Handler<AsyncResult<Void>> hndlr) {
    httpClientRequest.end(buffer, hndlr);
  }

  @Override
  public Future<Void> end() {
    return httpClientRequest.end();
  }

  @Override
  public void end(Handler<AsyncResult<Void>> hndlr) {
    httpClientRequest.end(hndlr);
  }

  @Override
  public HttpClientRequest setTimeout(long l) {
    httpClientRequest.setTimeout(l);
    return this;
  }

  @Override
  public HttpClientRequest pushHandler(Handler<HttpClientRequest> hndlr) {
    httpClientRequest.pushHandler(hndlr);
    return this;
  }

  @Override
  public boolean reset(long l) {
    return httpClientRequest.reset(l);
  }

  @Override
  public HttpConnection connection() {
    return httpClientRequest.connection();
  }

  @Override
  public HttpClientRequest writeCustomFrame(int i, int i1, Buffer buffer) {
    httpClientRequest.writeCustomFrame(i, i1, buffer);
    return this;
  }

  @Override
  public StreamPriority getStreamPriority() {
    return httpClientRequest.getStreamPriority();
  }

  @Override
  public Future<Void> write(Buffer t) {
    return httpClientRequest.write(t);
  }

  @Override
  public void write(Buffer t, Handler<AsyncResult<Void>> hndlr) {
    httpClientRequest.write(t, hndlr);
  }

  @Override
  public boolean writeQueueFull() {
    return httpClientRequest.writeQueueFull();
  }

  @Override
  public boolean isComplete() {
    return httpClientRequest.isComplete();
  }

  @Override
  public Future<HttpClientResponse> onComplete(Handler<AsyncResult<HttpClientResponse>> hndlr) {
    return httpClientRequest.onComplete(hndlr);
  }

  @Override
  public Handler<AsyncResult<HttpClientResponse>> getHandler() {
    return httpClientRequest.getHandler();
  }

  @Override
  public HttpClientResponse result() {
    return httpClientRequest.result();
  }

  @Override
  public Throwable cause() {
    return httpClientRequest.cause();
  }

  @Override
  public boolean succeeded() {
    return httpClientRequest.succeeded();
  }

  @Override
  public boolean failed() {
    return httpClientRequest.failed();
  }
  
}
