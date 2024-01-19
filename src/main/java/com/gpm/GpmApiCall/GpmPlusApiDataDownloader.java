package com.gpm.GpmApiCall;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	String plantsURL;
	String elementsURL;
	String datasourcesURL;
	String dataListURL;

	
	// Each Plant(Site) is identified by its ID and has a specific name
	//private Plant[] plants;
	
	

	public GpmPlusApiDataDownloader(
			PropertiesGpmPlus propertiesGpmPlus) {
		
		this.propertiesGpmPlus = propertiesGpmPlus;
		this.restTemplate = new RestTemplate();
	}


	public PropertiesGpmPlus getPropertiesGpmPlus() {
		return propertiesGpmPlus;
	}
	public void setPropertiesGpmPlus(PropertiesGpmPlus propertiesGpmPlus) {
		this.propertiesGpmPlus = propertiesGpmPlus;
	}

     /*
	  -----This method return the ID for a specific Plant given Name
	 */
	private int getPlantId_By_PlantName(String plantName) {

		int plant_ID = 0;
		if (fetchPlants() != null) {
			for (Plant plant : fetchPlants()) {
				if (plant.getName().equals(plantName)) {
					plant_ID =  plant.getId();
				}
			} 	
		}
		return plant_ID;
	}
	

     /*
      ------This method return the Identifier for a specific Element given Name
	 */
	private int getElementID_By_ElementName(String plantName, String elementName ) {

		int element_Identifier = 0;
		
		if (fetchPlantElements(plantName) != null) {
			for (Element element : fetchPlantElements(plantName)) {
				if (element.getName().equals(elementName)) {
					
					element_Identifier =  element.getIdentifier();
				}
			} 	
		}
		return element_Identifier;
	}
	
	
    /*
    ------This method return the dataSource ID for a specific dataSource , we need this dataSource to get data related to a site production per day
	*/
	private int getDataSourceID_By_DatasourceName(String plantName, String elementName, String dataSourceName) {
		
		int dataSourceId = 0;
		
		if (fetchElementDataSources(plantName, elementName)!= null) {
			
			for (DataSource dataSource : fetchElementDataSources(plantName,elementName)) {
				if (dataSource.getDataSourceName().equals(dataSourceName)) {
					
					dataSourceId =  dataSource.getDataSourceId();
				}
			}	
		}
		return dataSourceId;	
	}
	
	
    /*
    	------return the energy in kWh
	*/
	public double get_Energy_In_Kwh(String plantName, String elementName, String dataSourceName, String startDate, String endDate) {
		
		double kWh_Energy = 0;
		
		if (fetchDataList(plantName, elementName, dataSourceName, startDate, endDate)!= null) {
			
			for (DataList specificdataList : fetchDataList(plantName, elementName, dataSourceName, startDate, endDate)) {		
					
					kWh_Energy =  specificdataList.getValue();
			}	
		}
		return kWh_Energy;	
	}
	
	/*
	  ------return the date
	*/
	public String getDateReport(String plantName, String elementName, String dataSourceName, String startDate, String endDate) {
		
		String date = null;
		
		if (fetchDataList(plantName, elementName, dataSourceName, startDate, endDate)!= null) {
			
			for (DataList specificdataList : fetchDataList(plantName, elementName, dataSourceName, startDate, endDate)) {		
					
					date =  specificdataList.getDate();
			}	
		}
		return date;	
	}
	
	
    /*
    ------We need this method to get the correct value of the datasource Units from the API. We do not need the define this value manualy because it is defined from the API
	*/
	private String determineDatasourceUnits(String plantName, String elementName, String dataSourceName) {
		
		String units = null;
		
		if (fetchElementDataSources(plantName, elementName)!= null) {
			
			for (DataSource dataSource : fetchElementDataSources(plantName,elementName)) {
				if (dataSource.getDataSourceName().equals(dataSourceName)) {
					
					units =  dataSource.getUnits();
				}
			}	
		}
		return units;	
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
		
		   /*
			 JSON Response from the API
			    { 
				  "_alertIcon": "NotSpecified",
				  "ElementCount": 0,
				  "Id": 0,
				  "Name": "string",
				  "DatasourcesCount": 0,
				  "AlarmColor": 0,
				  "UniqueID": "00000000-0000-0000-0000-000000000000",
				  "Parameters": [
				    {
				      "Key": "string",
				      "Value": "string"
				    }
				  ]
				}
		   */
		
		plantsURL= this.propertiesGpmPlus.getGpmPlusWebApi().getUrl() 
				+ "/api/Plant";
		
		HttpEntity<HttpHeaders> request = build_the_request();
		
		ResponseEntity<Plant[]> response = restTemplate.exchange(
													plantsURL, 
													HttpMethod.GET, 
													request, 
													Plant[].class);   
		
		return response.getBody();
	}
	
	
	
	/*
	 * ----------------------- Fetch all Elements(devices) installed for a given Plants  for different nuru sites(plants)--------------------
	 * with this method we are getting data for all devices that are installed at a nuru site (Goma, Tadu and Faradje sites);we have to specify a given site name 
	 * for which we want to get elements that are installed
	 */
	public Element[] fetchPlantElements(String plantName){
		
		//static String elementsURL = "api/Plant/{4}/Element";
		elementsURL = this.propertiesGpmPlus.getGpmPlusWebApi().getUrl() 
				+ "/api/Plant"
				+ "/"+ getPlantId_By_PlantName(plantName)
				+ "/Element";
		
		HttpEntity<HttpHeaders> request = build_the_request();
		
//		LOG.info("\n\n----------------------------------------------------------------------------------------------------------");
//		LOG.info("Endpoint for the request : "+ elementsURL+"");
		
		ResponseEntity<Element[]> response = restTemplate.exchange(
													elementsURL, 
													HttpMethod.GET, 
													request, 
													Element[].class);  
		
		Element[] plantElementsFromGpmApi = response.getBody();
		
/*		System.out.println("[");
		
		if (plantElementsFromGpmApi != null) {
			for (Element element : plantElementsFromGpmApi) {
				
				System.out.println(" {\n"
						+ "  Identifier :"+ element.getIdentifier()
						+","
						+ "\n"
						+ "  UniqueID : "+ element.getUniqueID()
						+","
						+ "\n"
						+ "  Name : "+ element.getName()
						+","
						+ "\n"
						+ "  Type : "+ element.getType()
						+","
						+ "\n"
						+ "  TypeString : "+ element.getTypeString()
						+","
						+ "\n"
						+ "  ParentId : "+ element.getParentId()
						+"\n"
						+" }");
				
			}
			System.out.println("]\n");

		} 
*/		
		return plantElementsFromGpmApi;
	}
	
	
	
	/*
	 * ----------------Fetch all dataSource  for a given   plant's device (element)--------------------
	 * Each device has a list of dataSource , with this method we get all device's datasource.  we have to specify the site name  and the device name to get different 
	 * datasources so that we can choose the datasource that correspond to energy production (kwh).
	 */
	public DataSource[] fetchElementDataSources(String plantName, String elementName) {
		
		//String datasourcesURL = "api/Plant/{4}/Element/{8815}/Datasource";
		datasourcesURL =this.propertiesGpmPlus.getGpmPlusWebApi().getUrl() 
				+ "/api/Plant"
				+ "/" + getPlantId_By_PlantName(plantName)
				+ "/Element"
				+ "/" + getElementID_By_ElementName(plantName, elementName)
				+ "/Datasource";
		
		HttpEntity<HttpHeaders> request = build_the_request();
		
//		LOG.info("\n\n----------------------------------------------------------------------------------------------------------");
//		LOG.info("Endpoint for the request : "+ datasourcesURL+"");
		
		ResponseEntity<DataSource[]> response = restTemplate.exchange(
													datasourcesURL, 
													HttpMethod.GET, 
													request, 
													DataSource[].class);  
		
		DataSource[] deviceDataSources = response.getBody();
		
/*		System.out.println("[");
		
		if (deviceDataSources != null) {
			for (DataSource dataSource : deviceDataSources) {
				
				System.out.println(" {\n"
						+ "  ElementId :"+ dataSource.getElementId()
						+","
						+ "\n"
						+ "  DataSourceId : "+ dataSource.getDataSourceId()
						+","
						+ "\n"
						+ "  DataSourceName : "+ dataSource.getDataSourceName()
						+","
						+ "\n"
						+ "  Units : "+ dataSource.getUnits()
						+"\n"
						+" }");
				
			}
			System.out.println("]\n");
		}
*/
		return deviceDataSources;
	}
	
	
	/*
	 * ----------------Fetch all dataList--------------------
	 *  
	 */
	public DataList[] fetchDataList(String plantName, String elementName, String dataSourceName, String startDate, String endDate) {
		
		//String datasourcesURL = "api/DataList?dataSourceId=67054&startDate=1699837200&endDate=1700179199&aggregationType=11&grouping=day";
		dataListURL = this.propertiesGpmPlus.getGpmPlusWebApi().getUrl()
				+ "/api/DataList?dataSourceId=" + getDataSourceID_By_DatasourceName(plantName, elementName, dataSourceName) 
				+ "&startDate=" + getUnixTimestamp(startDate)
				+ "&endDate=" + getUnixTimestamp(endDate) 
				+ "&aggregationType=" + this.propertiesGpmPlus.getDataListParams().getAggregationType()
				+ "&grouping=" + this.propertiesGpmPlus.getDataListParams().getGrouping();
		
		HttpEntity<HttpHeaders> request = build_the_request();
		
//		LOG.info("\n\n----------------------------------------------------------------------------------------------------------");
//		LOG.info("Endpoint for the request : "+ dataListURL+"");
		
		ResponseEntity<DataList[]> response = restTemplate.exchange(
													dataListURL, 
													HttpMethod.GET, 
													request, 
													DataList[].class);  
		
		DataList[] dataLists = response.getBody();
		
/*		System.out.println("[");
		
		if (dataLists != null) {
			for (DataList dataList : dataLists) {
				
				System.out.println(" {\n"
						+ "  DataSourceId : "+ dataList.getDataSourceId()
						+","
						+ "\n"
						+ "  Date : "+ dataList.getDate()
						+","
						+ "\n"
						+ "  Value : "+ dataList.getValue()
						+"\n"
						+" }");
				
			}
			System.out.println("]\n");
		}
*/
		return dataLists;
	}
	


    /*
     * this method convert a given date time into unixtimestamp, we need this unixtimestamp value to get data for site production from endpoint  GPM PlusAPI
	*/
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




