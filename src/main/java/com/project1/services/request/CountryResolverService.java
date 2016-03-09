package com.project1.services.request;

import com.sun.jersey.api.client.ClientResponse;

public interface CountryResolverService {
    ClientResponse getCountry(String ip);
}
