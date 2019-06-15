package com.revolut.controller;

import io.micronaut.http.client.annotation.Client;

@Client(value = "/account")
public interface AccountTestClient extends AccountClient {

}
