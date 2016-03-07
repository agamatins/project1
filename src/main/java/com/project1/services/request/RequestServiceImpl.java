package com.project1.services.request;

import com.project1.utils.AppDefaults;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.stereotype.Service;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


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

    //since it's singletone by default - it should be thread safe!
    private ConcurrentMap<String, CircularFifoQueue<LocalDate>> countryRequestMap = new ConcurrentHashMap<>();

    @Override
    public boolean isSpamCompliant(String country) {
        //when it's failed:
        //1. country is in map
        //2. queue is full
        //3. difference between first and last is 1+ second
        //all otehr cases - compliant
        CircularFifoQueue<LocalDate> buff = countryRequestMap.getOrDefault(country, new CircularFifoQueue<>(AppDefaults.NUMBER_OF_SESSIONS_PER_SECOND));
        LocalDate now = LocalDate.now();
        buff.add(now);
        countryRequestMap.put(country, buff);
        if (!buff.isFull()){
            return true;
        } else {
            LocalDate first = buff.get(0);
            return ChronoUnit.SECONDS.between(first, now) < 1;
        }
    }
}
