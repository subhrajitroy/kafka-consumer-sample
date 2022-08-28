package com.roy.examples;

import feign.Headers;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "mockApiClient", url = "https://httpdemos.free.beeceptor.com")
public interface MockApiClient {

    @RequestMapping(method = RequestMethod.GET,path = "/check")
    String getHealthStatus();

    @RequestMapping(method = RequestMethod.PATCH,path = "/user/{userId}")
    @Headers({"X-HTTP-Method-Override","PATCH"})
    String updateUser(@PathVariable String userId);
}
