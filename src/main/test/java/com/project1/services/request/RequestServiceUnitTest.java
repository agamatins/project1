package com.project1.services.request;


import com.project1.services.request.RequestService;
import com.project1.services.request.RequestServiceImpl;
import com.project1.utils.AppDefaults;
import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class RequestServiceUnitTest {


    @Test
    public void testSpamCompliance_OK(){
        RequestService service = new RequestServiceImpl();
        IntStream.range(0, AppDefaults.NUMBER_OF_SESSIONS_PER_SECOND).parallel().forEach(
                nbr -> {
                    assertTrue(service.isSpamCompliant("LV"));
                }
        );
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(service.isSpamCompliant("LV"));
    }

    @Test
    public void testSpamCompliance_Fail(){
        RequestService service = new RequestServiceImpl();
        IntStream.range(0, AppDefaults.NUMBER_OF_SESSIONS_PER_SECOND).parallel().forEach(
                nbr -> {
                    assertTrue(service.isSpamCompliant("LV"));
                }
        );
        assertFalse(service.isSpamCompliant("LV"));
    }

    @Test
    public void testSpamCompliance_DifferentCountries(){
        RequestService service = new RequestServiceImpl();
        IntStream.range(0, AppDefaults.NUMBER_OF_SESSIONS_PER_SECOND).parallel().forEach(
                nbr -> {
                    assertTrue(service.isSpamCompliant("LV"));
                    assertTrue(service.isSpamCompliant("RU"));
                }
        );
        assertFalse(service.isSpamCompliant("LV"));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(service.isSpamCompliant("RU"));
        assertTrue(service.isSpamCompliant("LV"));
    }
}
