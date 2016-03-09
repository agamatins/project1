package com.project1.services.functional;


import com.project1.services.request.RequestService;
import com.project1.services.request.RequestServiceImpl;
import org.junit.Test;
import static org.junit.Assert.*;

public class RequestServiceTest {

    @Test
    public void testSpamCompliance_OK_SingleCountry() {
        RequestService service = new RequestServiceImpl();
        boolean result = service.isSpamCompliant("LV");
        assertTrue(result);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        result = service.isSpamCompliant("LV");
        assertTrue(result);

    }

    @Test
    public void testSpamCompliance_OK_DifferentCountries() {
        RequestService service = new RequestServiceImpl();
        boolean result = service.isSpamCompliant("LV");
        assertTrue(result);
        result = service.isSpamCompliant("RU");
        assertTrue(result);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        result = service.isSpamCompliant("LV");
        assertTrue(result);
        result = service.isSpamCompliant("RU");
        assertTrue(result);

    }

    @Test
    public void testSpamCompliance_Fail() {
        RequestService service = new RequestServiceImpl();
        boolean result = service.isSpamCompliant("LV");
        assertTrue(result);
        result = service.isSpamCompliant("LV");
        assertFalse(result);
    }

    @Test
    public void testSpamCompliance_OKAfterFail() {
        RequestService service = new RequestServiceImpl();
        boolean result = service.isSpamCompliant("LV");
        assertTrue(result);
        result = service.isSpamCompliant("LV");
        assertFalse(result);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        result = service.isSpamCompliant("LV");
        assertTrue(result);
    }
}
