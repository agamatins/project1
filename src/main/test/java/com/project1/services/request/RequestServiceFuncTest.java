package com.project1.services.request;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.header.InBoundHeaders;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CountryResolverServiceImpl.class, RequestServiceImpl.class})
public class RequestServiceFuncTest {
    @Mock
    CountryResolverServiceImpl countryResolverService;

    @Mock
    ClientResponse clientResponse;

    @InjectMocks
    RequestServiceImpl requestService;

    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCountry_Null_Response() {
        when(countryResolverService.getCountry(anyString())).thenReturn(null);
        assertEquals(requestService.getCountry("SOME_IP"), "LV");
    }
//
//    @Test
//    public void testGetCountry_OK() {
//        when(clientResponse.getStatus()).thenReturn(200);
//        when(clientResponse.getEntity(String.class)).thenReturn("US");
//        assertEquals(requestService.getCountry("US_IP"), "US");
//    }

}
