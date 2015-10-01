package com.liferay.demo.geolocation;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class Parser_311_requests_json {

	GeolocationDemoDataset parse() throws Exception {
		String name = "311_requests.json";

		InputStream in = this.getClass().getResourceAsStream(name);

		ObjectMapper m = new ObjectMapper();

		Request311[] entries = m.readValue(in, Request311[].class);

		return new GeolocationDemoDataset(entries);
	}

}
