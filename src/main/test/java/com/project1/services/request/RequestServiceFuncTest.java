package com.project1.services.request;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CountryResolverServiceImpl.class, RequestServiceImpl.class})
public class RequestServiceFuncTest {
    @Mock
    CountryResolverServiceImpl countryResolverService;

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

}
