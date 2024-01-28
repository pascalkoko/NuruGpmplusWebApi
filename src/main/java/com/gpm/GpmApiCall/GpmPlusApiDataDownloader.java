package com.gpm.GpmApiCall;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class GpmPlusApiDataDownloader {
	
	private static final String DATE_START ="2023-11-21 10:00:00 AM";
	private static final String DATE_END   = "2023-11-21 11:00:00 AM";
	
	//private static final Logger LOG = LoggerFactory.getLogger(GpmPlusApiDataDownloader.class);
	private RestTemplate restTemplate;
	private PropertiesGpmPlus propertiesGpmPlus;
	private String bearerToken;
    // this object will contains the report for all the 3 sites per day 
	Collection<ExcelDTO> gpmReports= new HashSet<ExcelDTO>();
    

	public GpmPlusApiDataDownloader(
			PropertiesGpmPlus propertiesGpmPlus) {
		
		this.propertiesGpmPlus = propertiesGpmPlus;
		this.restTemplate = new RestTemplate();
		this.bearerToken = generateGpmPlusBeareToken();
	}
     /*
	  -----return a collection of 
	 */
	public Collection<ExcelDTO>  build_GPM_Report() {
		long strDate = System.currentTimeMillis();
		Plant[] plants = fetchPlants();
		
		for (Plant plant : plants) {
			String plantName = plant.Name();
		    ExcelDTO excelDTO = new ExcelDTO();
			excelDTO.setDate(DATE_START);
			excelDTO.setSiteName(plantName);
			Element[] plantElements = fetchPlantElements(plant.Id());
			for (Element  element : plantElements) {
				DataSource[] element_Datasources;	
				
				if (element.Name().equals("Main Meter")) {
					element_Datasources = fetchElementDataSources(plant.Id(), element.Identifier());
					for (DataSource dataSource : element_Datasources) {
						if (dataSource.DataSourceName().equals("EXPORTED ACTIVE ENERGY")) {
							excelDTO.setKwh_MainMeter(get_kwh(dataSource.DataSourceId()));
						}
					}
				}else if (element.Name().equals("Solar PV Meter")) {
					element_Datasources = fetchElementDataSources(plant.Id(), element.Identifier());	
					for (DataSource dataSource : element_Datasources) {
						if (dataSource.DataSourceName().equals("EXPORTED ACTIVE ENERGY")) {	
							excelDTO.setKwh_SolarMeter(get_kwh(dataSource.DataSourceId()));
						}
					}
				}else if (element.Name().equals("Genset 1 Meter")) {
					element_Datasources = fetchElementDataSources(plant.Id(), element.Identifier());					
					for (DataSource dataSource : element_Datasources) {						
						if (dataSource.DataSourceName().equals("EXPORTED ACTIVE ENERGY")) {							
							double kwh_Genset1 = get_kwh(dataSource.DataSourceId());							
							excelDTO.setKwh_Genset1(kwh_Genset1);
						}
					}
				  }else if (element.Name().equals("Genset 2 Meter")) {				
					    //double kwh_Genset2 =0;
						element_Datasources = fetchElementDataSources(plant.Id(), element.Identifier());
						for (DataSource dataSource : element_Datasources) {	
							if (dataSource.DataSourceName().equals("EXPORTED ACTIVE ENERGY")) {
								double kwh_Genset2 = get_kwh(dataSource.DataSourceId());
								excelDTO.setKwh_Genset2(kwh_Genset2);
								//System.out.println( "\n "+plantName+" kwh Genset_2 :"+kwh_Genset2);
							}
						}
					  }	
				//excelDTO.setKwh_Genset(kwh_Genset1 + kwh_Genset2);
			}	
			if (excelDTO.getSiteName() != null) {
				gpmReports.add(excelDTO);
			}
		}
		System.out.println(" \n Time taken to call all endpoints and retreive final data :"+((System.currentTimeMillis() - strDate)));
		return gpmReports;
	}

	
	//  return the value  of energy in kwh  for each element
	private double get_kwh(int datasourceID) {		
		DataList[] dataLists = fetchDataList(datasourceID, DATE_START, DATE_END);
		if (dataLists == null || dataLists.length != 1) {  
			throw new RuntimeException(" The request fetch dataList returned null or more than one object");
		}
		return dataLists[0].Value();
	}
	
	
	//build the request 
	private  HttpEntity<HttpHeaders> buildRequest() {
		String authorizationHeader = bearerToken;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization",authorizationHeader);
		return new HttpEntity<>(headers);
	}

	

	//Fetch all Plants from GpmPlus API
	public Plant[] fetchPlants() {
		String plantsURL= this.propertiesGpmPlus.getAuthentication().getUrl() 
				+ "/api/Plant";
		
		HttpEntity<HttpHeaders> request = buildRequest();
		//long start =System.currentTimeMillis();				
		ResponseEntity<Plant[]> response = restTemplate.exchange(
													plantsURL, 
													HttpMethod.GET, 
													request, 
													Plant[].class);   
		
		//System.out.println("plants  fetch time: " + (System.currentTimeMillis()-start)+ "    "+ plantsURL);			
		return response.getBody();
	}
	

	//Fetch all plant Elements
	public Element[] fetchPlantElements(int plant_ID){
		//static String elementsURL = "api/Plant/{4}/Element";
		String elementsURL = this.propertiesGpmPlus.getAuthentication().getUrl() 
				+ "/api/Plant"
				+ "/"+ plant_ID
				+ "/Element";
		
		HttpEntity<HttpHeaders> request = buildRequest();
		//long start =System.currentTimeMillis();	
		ResponseEntity<Element[]> response = restTemplate.exchange(
													elementsURL, 
													HttpMethod.GET, 
													request, 
													Element[].class);  
		
		//System.out.println("Elements  fetch time: " + (System.currentTimeMillis()-start)+ "    "+ elementsURL);		
		Element[] plantElementsFromGpmApi = response.getBody();	
		return plantElementsFromGpmApi;
	}
	
	
	//Fetch all datasource for specific  element Identifier
	public DataSource[] fetchElementDataSources(int plant_ID, int element_ID) {
		//String datasourcesURL = "api/Plant/{4}/Element/{8815}/Datasource";
		String datasourcesURL =this.propertiesGpmPlus.getAuthentication().getUrl() 
				+ "/api/Plant"
				+ "/" + plant_ID
				+ "/Element"
				+ "/" + element_ID
				+ "/Datasource";		
		
		HttpEntity<HttpHeaders> request = buildRequest();
		//long start =System.currentTimeMillis();				
		ResponseEntity<DataSource[]> response = restTemplate.exchange(
													datasourcesURL, 
													HttpMethod.GET, 
													request, 
													DataSource[].class);  
		
		//System.out.println("datasource  fetch time: " + (System.currentTimeMillis()-start) + "    "+ datasourcesURL);	
		DataSource[] deviceDataSources = response.getBody();
		return deviceDataSources;
	}
	

	//Fetch  dataList for a given datasource ID 
	public DataList[] fetchDataList(int dataSource_ID, String startDate, String endDate) {
		int aggregationType = 11;
		String grouping ="day";
		//String datasourcesURL = "api/DataList?dataSourceId=67054&startDate=1699837200&endDate=1700179199&aggregationType=11&grouping=day";
		String dataListURL = this.propertiesGpmPlus.getAuthentication().getUrl()
				+ "/api/DataList?dataSourceId=" + dataSource_ID 
				+ "&startDate=" + getUnixTimestamp(startDate)
				+ "&endDate=" + getUnixTimestamp(endDate) 
				+ "&aggregationType=" + aggregationType
				+ "&grouping=" + grouping;
		
		HttpEntity<HttpHeaders> request = buildRequest();
		//long start =System.currentTimeMillis();			
		ResponseEntity<DataList[]> response = restTemplate.exchange(
													dataListURL, 
													HttpMethod.GET, 
													request, 
													DataList[].class);  
		
		//System.out.println("dataList fetch time: " + (System.currentTimeMillis()-start) + "    "+ dataListURL);
		DataList[] dataLists = response.getBody();
		return dataLists;
	}
	
	// Convert a given dataTime into timestamp
	private static long getUnixTimestamp(String strDateTime) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
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
	
	
	
	private  String generateGpmPlusBeareToken(){		
		String authenticationURL =this.propertiesGpmPlus.getAuthentication().getUrl()+"/api/Account/Token";
		String username = this.propertiesGpmPlus.getAuthentication().getUsername();
		String password = this.propertiesGpmPlus.getAuthentication().getPassword();
					
		HashMap< String, String> map = new HashMap<>();
		map.put("username", username);
		map.put("password", password);	
		//set headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);			
		// build the request body as a Json Object  to generate the Token Credentials must be sent as a json Object {"username" :"dgdgdfgg", "password":"dfggfgdg"}
		JSONObject json = new JSONObject();
		json.putAll(map);
		
		HttpEntity<JSONObject>  request = new HttpEntity<JSONObject>(json,headers);
			
		ResponseEntity<TokenRet> tokenRetResponse = restTemplate.postForEntity(  
																authenticationURL, 
																request, 
																TokenRet.class);
			if (tokenRetResponse.getBody() != null) {
				TokenRet token = tokenRetResponse.getBody();
				// "bearer AQAAANCMnd8BFdERjHoA......."
				return token.TokenType() + " " + token.AccessToken();
			} else {
				throw new RuntimeException("GpmPlus didn't deliver a Bearer token response body.");
			}
	}
}



@JsonIgnoreProperties(ignoreUnknown = true)
record Plant(
		Integer Id,
		String Name){
}

@JsonIgnoreProperties(ignoreUnknown = true)
record  Element (
		Integer Identifier, 
		String Name) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record  DataSource(
		Integer ElementId,
		Integer DataSourceId,
		String DataSourceName) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record DataList(
		Integer DataSourceId,
		String Date,
		double Value) {
}


@JsonIgnoreProperties(ignoreUnknown = true)
 record TokenRet(
		String TokenType,
		String AccessToken) {

}






