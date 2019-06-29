package com.revolut.controller;

import io.micronaut.http.client.annotation.Client;

@Client(value = "/account", errorType = RuntimeException.class)
public interface AccountTestClient extends AccountClient {

}
