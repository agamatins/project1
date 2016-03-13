package com.project1.services.request;

import com.project1.utils.AppDefaults;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Service("requestService")
public class RequestServiceImpl implements RequestService {
    final static Logger logger = LogManager.getLogger(RequestServiceImpl.class);

    @Autowired
    CountryResolverService countryResolverService;

    @Override
    public String getCountry(String ip) {
        if (org.springframework.util.StringUtils.isEmpty(ip)){
            logger.info("Ip not detected from request.");
            return AppDefaults.DEFAULT_COUNTRY;
        }

        ClientResponse response = countryResolverService.getCountry(ip);

        //we might not even get any response
        if (response == null) {
            logger.info("Cannot get response from Country Detection Service");
            return AppDefaults.DEFAULT_COUNTRY;
        }

        //or we might get something weird in response
        if (response.getStatus() != 200) {
            logger.info("Wrong response from Country Detection Service " + response.getStatus());
            return AppDefaults.DEFAULT_COUNTRY;
        }

        String country = response.getEntity(String.class);
        //for all abnormal cases this service returns XX. Since we don't have such country - we'd better return out default country
        return (country.equals("XX") ? AppDefaults.DEFAULT_COUNTRY : country);
    }

    //since it's singletone by default - it should be thread safe!
    //cotnains CircularFifoQueue of timestamps of N last requests for each country ever trying to apply.
    //New request replaces added to the end of queue, first one is pushed out and all other moved one step towards the beginning
    private ConcurrentMap<String, CircularFifoQueue<LocalTime>> countryRequestMap = new ConcurrentHashMap<>();

    @Override
    public boolean isSpamCompliant(String country) {
        //when it's failed:
        //1. country is in map
        //2. queue is full
        //3. difference between first and last is 1+ second
        //all otehr cases - compliant
        synchronized (this) {
            CircularFifoQueue<LocalTime> buff = countryRequestMap.getOrDefault(country, new CircularFifoQueue<>(AppDefaults.NUMBER_OF_SESSIONS_PER_SECOND + 1));
            LocalTime now = LocalTime.now();
            buff.add(now);
            countryRequestMap.put(country, buff);
            if (!buff.isAtFullCapacity()) {
                return true;
            } else {
                LocalTime first = buff.get(0);
                logger.debug(country + ": Millis diff: " + ChronoUnit.MILLIS.between(first, now));
                return ChronoUnit.MILLIS.between(first, now) > 1000;
            }
        }
    }
}
