package com.project1.services.request;

import com.project1.utils.AppDefaults;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.springframework.stereotype.Service;

@Service("countryResolverService")
public class CountryResolverServiceImpl implements CountryResolverService{

    public ClientResponse getCountry(String ip) {
        //hello Jersey, long time no seen;)
        Client client = Client.create();
        WebResource webResource = client.resource(AppDefaults.REQUEST_COUNTRY_BASE_URL + ip);
        return webResource.accept("text/html").get(ClientResponse.class);
    }

}
