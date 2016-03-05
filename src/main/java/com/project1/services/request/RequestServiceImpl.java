package com.project1.services.request;

import com.project1.utils.AppDefaults;
import org.springframework.stereotype.Service;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;


@Service("requestService")
public class RequestServiceImpl implements RequestService {

    @Override
    public String getCountry(String ip) {
        //hello Jersey, long time no seen;)
        Client client = Client.create();
        WebResource webResource = client.resource(AppDefaults.REQUEST_COUNTRY_BASE_URL + ip);
        ClientResponse response = webResource.accept("text/html").get(ClientResponse.class);

        //we might not even get any response
        if (response == null) {
            System.out.println("Cannot get response from Country Detection Service");
            return AppDefaults.DEFAULT_COUNTRY;
        }

        //or we might get something weird in response
        if (response.getStatus() != 200) {
            System.out.println("Wrong response from Country Detection Service " + response.getStatus());
            return AppDefaults.DEFAULT_COUNTRY;
        }

        String country = response.getEntity(String.class);
        //for all abnormal cases this service returns XX. Since we don't have such country - we'd better return out default country
        return (country.equals("XX") ? AppDefaults.DEFAULT_COUNTRY : country);
    }
}
