package com.gpm.GpmApiCall;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class GpmApiCall {

	private static final Logger LOG = LoggerFactory.getLogger(GpmApiCall.class);

	private RestTemplate restTemplate;
	private PropertiesGpmPlus propertiesGpmPlus;
	
	
	// different Element's datasourceId we need for each site (Goma, Tadu and Faradje) to get GPM Data
	int mainMeter_dataSourceId = 67054;
	int solar_dataSourceId = 67017;
	int genset1_dataSourceId = 67091;
	int genset2_dataSourceId = 67128;

	long startDate = getUnixTimestamp("2023-11-15 12:00:00");
	long endDate = getUnixTimestamp("2023-11-16 11:59:59");
	//int aggregationType = 11;
	//String grouping = "day";

	static String elementsURL = "api/Plant/{4}/Element";
	static String datasourcesURL = "api/Plant/{4}/Element/{1}/Datasource";
	static String lastDataURL = "api/Plant/{4}/LastData";

	public GpmApiCall(
			RestTemplate restTemplate, 
			PropertiesGpmPlus propertiesGpmPlus) {
		this.restTemplate = restTemplate;
		this.propertiesGpmPlus = propertiesGpmPlus;
	}


	
	/*
	 * ------------------------------------ BUILD COMPLETE URL FOR Fecthing Data related to Site Production-------------------------------------------
	 * we need this method because we are getting data the complete URL to get the information we need for Kwh Energy production data for each element
	 */	
	private  String get_API_URL_By_DataSourceId(int datasourceId, 
											long startDate, 
											long endDate) {

		String url = propertiesGpmPlus.getGpmPlusWebApi().getUrl()
				+ "/api/DataList?dataSourceId=" + datasourceId 
				+ "&startDate=" + startDate
				+ "&endDate=" + endDate 
				+ "&aggregationType=" +propertiesGpmPlus.getDataListParams().getAggregationType()
				+ "&grouping=" + propertiesGpmPlus.getDataListParams().getGrouping();

		return url;
	}
	

	// this method convert a given date time into unixtimestamp, we need this unixtimestamp value to test the data endpoint from GPM PlusAPI
	private static long getUnixTimestamp(String strDateTime) {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
		long unixTimestamp = 0;
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+02:00"));

		try {
			unixTimestamp = dateFormat.parse(strDateTime).getTime();
			unixTimestamp = unixTimestamp / 1000;

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return unixTimestamp;
	 }
	
	
	public void printurl() {
		LOG.info(get_API_URL_By_DataSourceId(mainMeter_dataSourceId, startDate, endDate));
		System.out.println(mainMeter_dataSourceId);
	}
	
}


