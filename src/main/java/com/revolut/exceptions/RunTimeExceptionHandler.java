package com.revolut.exceptions;

import com.revolut.domain.ResponseType;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;

@Produces
@Prototype
@Requires(classes = {RuntimeException.class})
public class RunTimeExceptionHandler implements
    io.micronaut.http.server.exceptions.ExceptionHandler<RuntimeException, HttpResponse> {

  @Override
  public HttpResponse handle(HttpRequest request, RuntimeException exception) {
    ResponseType responseWireType = ResponseType.builder().message(exception.getLocalizedMessage()).build();
    return HttpResponse.ok(responseWireType);
  }
}
