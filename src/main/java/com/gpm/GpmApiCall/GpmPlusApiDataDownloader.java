package com.gpm.GpmApiCall;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class GpmPlusApiDataDownloader {
	
	private static final Logger LOG = LoggerFactory.getLogger(GpmPlusApiDataDownloader.class);
	RestTemplate restTemplate;
	private PropertiesGpmPlus propertiesGpmPlus;
	
	String startDate ="2023-11-01 10:00:00 AM";
	String  endDate = "2023-11-01 11:00:00 AM";
	Collection<GpmExcelDTO> excelDTOs;
    GpmExcelDTO excelDTO;
    String dateReport;
    

	public GpmPlusApiDataDownloader(
			PropertiesGpmPlus propertiesGpmPlus) {
		
		this.propertiesGpmPlus = propertiesGpmPlus;
		this.restTemplate = new RestTemplate();
	}
     /*
	  -----return a collection of 
	 */
	public Collection<GpmExcelDTO>  get_Gpm_Reports() {
		
		excelDTO = new GpmExcelDTO();
		excelDTOs = new HashSet<GpmExcelDTO>();
		
		long strDate = System.currentTimeMillis();
		
		Plant[] plants = fetchPlants();
		DataSource[] element_Datasources;
		
		for (Plant plant : plants) {
			
			if (plant.getName().equals("KIVUE")) {				
				
				GpmExcelDTO goma_gpmReport = new GpmExcelDTO();	
				goma_gpmReport.setSiteName(plant.getName());
				
				double kwh_Genset1=0;
				double kwh_Genset2 = 0;
				
				Element[] plantElements = fetchPlantElements(plant.getId());
			
				for (Element element : plantElements) {
					
					if (element.getName().equals("Main Meter")) {
						
						element_Datasources = fetchElementDataSources(plant.getId(), element.getIdentifier());
						
						for (DataSource dataSource : element_Datasources) {
							if (dataSource.getDataSourceName().equals("EXPORTED ACTIVE ENERGY")) {
								
								int dataSource_ID = dataSource.getDataSourceId();
								
								goma_gpmReport.setKwh_MainMeter(get_kwh(dataSource_ID, element));						}
						}
						
					}else if (element.getName().equals("PV Generation Meter")) {
						
						element_Datasources = fetchElementDataSources(plant.getId(), element.getIdentifier());
						
						for (DataSource dataSource : element_Datasources) {
							if (dataSource.getDataSourceName().equals("EXPORTED ACTIVE ENERGY")) {
								
								int dataSource_ID = dataSource.getDataSourceId();
								
								goma_gpmReport.setKwh_SolarMeter(get_kwh(dataSource_ID, element));
							}
						}
						
					}else if (element.getName().equals("Genset 1 Meter")) {
						
						element_Datasources = fetchElementDataSources(plant.getId(), element.getIdentifier());
						
						for (DataSource dataSource : element_Datasources) {
							if (dataSource.getDataSourceName().equals("EXPORTED ACTIVE ENERGY")) {
								
								int dataSource_ID = dataSource.getDataSourceId();
								
								kwh_Genset1 = get_kwh(dataSource_ID, element);
						
							}
						}
						
					}else if (element.getName().equals("Genset 2 Meter")) {
						
						element_Datasources = fetchElementDataSources(plant.getId(), element.getIdentifier());
						
						for (DataSource dataSource : element_Datasources) {
							if (dataSource.getDataSourceName().equals("EXPORTED ACTIVE ENERGY")) {
								
								int dataSource_ID = dataSource.getDataSourceId();
								kwh_Genset2 = get_kwh(dataSource_ID, element);
							}
						}
						
					}	
					goma_gpmReport.setKwh_Genset(kwh_Genset1 + kwh_Genset2);
					goma_gpmReport.setDate(dateReport);
					excelDTOs.add(goma_gpmReport);
				}
				
				
			}else if (plant.getName().equals("Tadu") || plant.getName().equals("Faradje")) {
				
				GpmExcelDTO tadu_gpmReport = new GpmExcelDTO();	
				GpmExcelDTO faradje_gpmReport = new GpmExcelDTO();
				
				if (plant.getName().equals("Tadu")) {

					tadu_gpmReport.setSiteName(plant.getName());
					
				} else if (plant.getName().equals("Faradje")) {

					faradje_gpmReport.setSiteName(plant.getName());
				}
				
				Element[] elements = fetchPlantElements(plant.getId());
				
				for (Element element : elements) {
					if (element.getName().equals("Meter Load")) {
						
						element_Datasources = fetchElementDataSources(plant.getId(), element.getIdentifier());
						
						for (DataSource dataSource : element_Datasources) {
							if (dataSource.getDataSourceName().equals("EXPORTED ACTIVE ENERGY")) {
								
								int dataSource_ID = dataSource.getDataSourceId();
								
								if (plant.getName().equals("Tadu")) {
									
									tadu_gpmReport.setKwh_MainMeter(get_kwh(dataSource_ID, element));
									
								} else if (plant.getName().equals("Faradje")){
									
									faradje_gpmReport.setKwh_MainMeter(get_kwh(dataSource_ID, element));
								}
							}
						}
						
					}else if (element.getName().equals("Meter Solar PV")) {
						
						element_Datasources = fetchElementDataSources(plant.getId(), element.getIdentifier());
						for (DataSource dataSource : element_Datasources) {
							if (dataSource.getDataSourceName().equals("EXPORTED ACTIVE ENERGY")) {
								
								int dataSource_ID = dataSource.getDataSourceId();
								
								if (plant.getName().equals("Tadu")) {
									
									tadu_gpmReport.setKwh_SolarMeter(get_kwh(dataSource_ID, element));
									
								} else if (plant.getName().equals("Faradje")){
									
									faradje_gpmReport.setKwh_SolarMeter(get_kwh(dataSource_ID, element));
								}
							}
						}
						
					}else if (element.getName().equals("Meter Diesel Generator")) {
						
						element_Datasources = fetchElementDataSources(plant.getId(), element.getIdentifier());
						for (DataSource dataSource : element_Datasources) {
							if (dataSource.getDataSourceName().equals("EXPORTED ACTIVE ENERGY")) {
								
								int dataSource_ID = dataSource.getDataSourceId();
								
								if (plant.getName().equals("Tadu")) {
									
									tadu_gpmReport.setKwh_Genset(get_kwh(dataSource_ID, element));
									
								} else if (plant.getName().equals("Faradje")){
									
									faradje_gpmReport.setKwh_Genset(get_kwh(dataSource_ID, element));
								}
								
							}
						}
						
					}
				}
				tadu_gpmReport.setDate(dateReport); 
				faradje_gpmReport.setDate(dateReport);
				excelDTOs.add(tadu_gpmReport); 
				excelDTOs.add(faradje_gpmReport);
			}

			
		}
		System.out.println(" \n Time taken to call all endpoints and retreive final data :"+((System.currentTimeMillis() - strDate)));
		return excelDTOs;
	}


	//  return the real value  of energy in kwh  for each element
	
	private double get_kwh(int datasourceID, Element element) {

		DataList[] dataLists = fetchDataList(datasourceID, startDate, endDate);
		if (dataLists != null && dataLists.length ==1) {    
	     
			dateReport = dataLists[0].getDate(); 
			
		}else {
			throw new RuntimeException(" The request fetch dataList returned more than one object");
		}
		return dataLists[0].getValue();
	}
	
	
	 /*
    	------build the request
	*/ 
	private  HttpEntity<HttpHeaders> build_the_request() {
		
		String authorizationHeader = "Bearer "
							+this.propertiesGpmPlus.getGpmPlusWebApi().getAccessToken();
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization",authorizationHeader);
		
		return new HttpEntity<>(headers);
	}

	
	/*
	 * ----------------------- Fetch all Plants from GpmPlus API--------------------
	 * with this method we are getting data for all devices that are installed at a nuru site (Goma, Tadu and Faradje sites); 
	 * 
	 */ 
	public Plant[] fetchPlants() {
		
		String plantsURL= this.propertiesGpmPlus.getGpmPlusWebApi().getUrl() 
				+ "/api/Plant";
		
		HttpEntity<HttpHeaders> request = build_the_request();
//		long start =System.currentTimeMillis();				
		ResponseEntity<Plant[]> response = restTemplate.exchange(
													plantsURL, 
													HttpMethod.GET, 
													request, 
													Plant[].class);   
		
//		System.out.println("plants  fetch time: " + (System.currentTimeMillis()-start)+ "    "+ plantsURL);			
		return response.getBody();
	}
	
	
	
	/*
	 * ----------------Fetch all plant Elements--------------------
	 *  
	 */
	public Element[] fetchPlantElements(int plant_ID){
		
		//static String elementsURL = "api/Plant/{4}/Element";
		String elementsURL = this.propertiesGpmPlus.getGpmPlusWebApi().getUrl() 
				+ "/api/Plant"
				+ "/"+ plant_ID
				+ "/Element";
		
		HttpEntity<HttpHeaders> request = build_the_request();

//		long start =System.currentTimeMillis();	
		ResponseEntity<Element[]> response = restTemplate.exchange(
													elementsURL, 
													HttpMethod.GET, 
													request, 
													Element[].class);  
		
//		System.out.println("Elements  fetch time: " + (System.currentTimeMillis()-start)+ "    "+ elementsURL);		
		Element[] plantElementsFromGpmApi = response.getBody();	
		return plantElementsFromGpmApi;
	}
	
	
	
	/*
	 * ----------------Fetch all dataSources--------------------
	 *  
	 */
	public DataSource[] fetchElementDataSources(int plant_ID, int element_ID) {
		
		//String datasourcesURL = "api/Plant/{4}/Element/{8815}/Datasource";
		String datasourcesURL =this.propertiesGpmPlus.getGpmPlusWebApi().getUrl() 
				+ "/api/Plant"
				+ "/" + plant_ID
				+ "/Element"
				+ "/" + element_ID
				+ "/Datasource";
		
			
		
		HttpEntity<HttpHeaders> request = build_the_request();
//		long start =System.currentTimeMillis();				
		ResponseEntity<DataSource[]> response = restTemplate.exchange(
													datasourcesURL, 
													HttpMethod.GET, 
													request, 
													DataSource[].class);  
		
//		System.out.println("datasource  fetch time: " + (System.currentTimeMillis()-start) + "    "+ datasourcesURL);	
		DataSource[] deviceDataSources = response.getBody();
		return deviceDataSources;
	}
	
	
	/*
	 * ----------------Fetch all dataList--------------------
	 *  
	 */
	public DataList[] fetchDataList(int dataSource_ID, String startDate, String endDate) {
		
		int aggregationType = 11;
		String grouping ="day";
		
		//String datasourcesURL = "api/DataList?dataSourceId=67054&startDate=1699837200&endDate=1700179199&aggregationType=11&grouping=day";
		String dataListURL = this.propertiesGpmPlus.getGpmPlusWebApi().getUrl()
				+ "/api/DataList?dataSourceId=" + dataSource_ID 
				+ "&startDate=" + getUnixTimestamp(startDate)
				+ "&endDate=" + getUnixTimestamp(endDate) 
				+ "&aggregationType=" + aggregationType
				+ "&grouping=" + grouping;
		
		HttpEntity<HttpHeaders> request = build_the_request();
//		long start =System.currentTimeMillis();			
		ResponseEntity<DataList[]> response = restTemplate.exchange(
													dataListURL, 
													HttpMethod.GET, 
													request, 
													DataList[].class);  
		
//		System.out.println("dataList fetch time: " + (System.currentTimeMillis()-start) + "    "+ dataListURL);
		DataList[] dataLists = response.getBody();
		return dataLists;
	}
	


    /*
     * -----------------Convert a given dateTime into unixtimestamp --------------------------------
	*/
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
}



@JsonIgnoreProperties(ignoreUnknown = true)
class Plant{
	
	@JsonProperty("Id")
	private Integer Id;
	
	@JsonProperty("Name")
	private String Name;
	
	public Integer getId() {
		return Id;
	}
	public void setId(Integer id) {
		Id = id;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
}



@JsonIgnoreProperties(ignoreUnknown = true)
class Element{
	
	@JsonProperty("Identifier")
	private Integer Identifier;
	@JsonProperty("UniqueID")
	private String UniqueID;
	@JsonProperty("Name")
	private String name;
	@JsonProperty("Type")
	private Integer Type;
	@JsonProperty("TypeString")
	private String TypeString;
	@JsonProperty("ParentId")
	private Integer ParentId;
	public Integer getIdentifier() {
		return Identifier;
	}
	public void setIdentifier(Integer identifier) {
		Identifier = identifier;
	}
	public String getUniqueID() {
		return UniqueID;
	}
	public void setUniqueID(String uniqueID) {
		UniqueID = uniqueID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getType() {
		return Type;
	}
	public void setType(Integer type) {
		Type = type;
	}
	public String getTypeString() {
		return TypeString;
	}
	public void setTypeString(String typeString) {
		TypeString = typeString;
	}
	public Integer getParentId() {
		return ParentId;
	}
	public void setParentId(Integer parentId) {
		ParentId = parentId;
	}
}


@JsonIgnoreProperties(ignoreUnknown = true)
class DataSource{
	
	@JsonProperty("ElementId")
	private Integer elementId;
	@JsonProperty("DataSourceId")
	private Integer dataSourceId;
	@JsonProperty("DataSourceName")
	private String dataSourceName;
	@JsonProperty("Units")
	private String Units;
	
	public Integer getElementId() {
		return elementId;
	}
	public void setElementId(Integer elementId) {
		this.elementId = elementId;
	}
	public Integer getDataSourceId() {
		return dataSourceId;
	}
	public void setDataSourceId(Integer dataSourceId) {
		this.dataSourceId = dataSourceId;
	}
	public String getDataSourceName() {
		return dataSourceName;
	}
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	public String getUnits() {
		return Units;
	}
	public void setUnits(String units) {
		Units = units;
	}
}


@JsonIgnoreProperties(ignoreUnknown = true)
class DataList{
	
	@JsonProperty("DataSourceId")
	private Integer dataSourceId;
	@JsonProperty("Date")
	private String date;
	@JsonProperty("Value")
	private double value;
	public Integer getDataSourceId() {
		return dataSourceId;
	}
	public void setDataSourceId(Integer dataSourceId) {
		this.dataSourceId = dataSourceId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
}

class GpmExcelDTO {
	
	String date;
	String siteName;
	double kwh_MainMeter;
	double kwh_SolarMeter;
	double kwh_Genset;
	
	
	public GpmExcelDTO() {
	}
	
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public double getKwh_MainMeter() {
		return kwh_MainMeter;
	}
	public void setKwh_MainMeter(double kwh_MainMeter) {
		this.kwh_MainMeter = kwh_MainMeter;
	}
	public double getKwh_SolarMeter() {
		return kwh_SolarMeter;
	}
	public void setKwh_SolarMeter(double kwh_SolarMeter) {
		this.kwh_SolarMeter = kwh_SolarMeter;
	}
	public double getKwh_Genset() {
		return kwh_Genset;
	}
	public void setKwh_Genset(double kwh_Genset) {
		this.kwh_Genset = kwh_Genset;
	}
	
	
}




