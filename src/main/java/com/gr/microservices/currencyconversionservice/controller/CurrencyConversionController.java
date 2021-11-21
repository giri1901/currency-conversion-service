package com.gr.microservices.currencyconversionservice.controller;

import com.gr.microservices.currencyconversionservice.bean.CurrencyConversion;
import com.gr.microservices.currencyconversionservice.proxy.CurrencyExchangeProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CurrencyConversionController {

    @Autowired
    CurrencyExchangeProxy exchangeProxy;

    @GetMapping("currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion findCurrencyConversion(@NonNull @PathVariable String from, @NonNull @PathVariable String to
            , @PathVariable BigDecimal quantity) {
            CurrencyConversion currencyConversion;
        //currencyConversion = new CurrencyConversion(10001L, from, to,  quantity, BigDecimal.ONE, BigDecimal.TEN, "localhost:8000");
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);
        ResponseEntity<CurrencyConversion> forEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversion.class, uriVariables);
        CurrencyConversion currencyConversionResponse = forEntity.getBody();
        currencyConversion = new CurrencyConversion(currencyConversionResponse.getId(), from, to,  quantity, currencyConversionResponse.getConversionMultiple() , quantity.multiply(currencyConversionResponse.getConversionMultiple()), currencyConversionResponse.getEnvironment());
        return currencyConversion;
    }

    @GetMapping("currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion findCurrencyConversionFeign(@NonNull @PathVariable String from, @NonNull @PathVariable String to
            , @PathVariable BigDecimal quantity) {
        CurrencyConversion currencyConversion;

        CurrencyConversion currencyConversionResponse = exchangeProxy.getCurrencyExchangeInfo(from, to);
        currencyConversion = new CurrencyConversion(currencyConversionResponse.getId(), from, to,  quantity, currencyConversionResponse.getConversionMultiple() , quantity.multiply(currencyConversionResponse.getConversionMultiple()), currencyConversionResponse.getEnvironment());
        return currencyConversion;
    }
}
