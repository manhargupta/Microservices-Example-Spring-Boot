package com.nagarro.microservice.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CurrencyExchangeServiceProxy currencyExchangeServiceProxy;

	@GetMapping("/currency-convertor/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {

		final Map<String, String> uriVariable = new HashMap<>();
		uriVariable.put("from", from);
		uriVariable.put("to", to);
		final ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate().getForEntity(
				"http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversionBean.class,
				uriVariable);

		final CurrencyConversionBean resp = responseEntity.getBody();

		logger.info("{}", resp);
		return new CurrencyConversionBean(resp.getId(), resp.getFrom(), resp.getTo(), resp.getConversionMultiple(),
				quantity, quantity.multiply(resp.getConversionMultiple()), resp.getPort());
	}

	@GetMapping("/currency-convertor-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrencyFeign(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {

		final CurrencyConversionBean resp = currencyExchangeServiceProxy.retrieveExchangeValue(from, to);

		return new CurrencyConversionBean(resp.getId(), resp.getFrom(), resp.getTo(), resp.getConversionMultiple(),
				quantity, quantity.multiply(resp.getConversionMultiple()), resp.getPort());
	}
}
