package com.gpm.GpmApiCall;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PropertiesGpmPlus.class)
public class GpmApiCallApplication implements CommandLineRunner{
	
	private GpmPlusApiDataDownloader gpmPlusApiDataDownloader;
	
	public GpmApiCallApplication(GpmPlusApiDataDownloader gpmPlusApiDataDownloader) {
		this.gpmPlusApiDataDownloader = gpmPlusApiDataDownloader;
	}

	
	public static void main(String[] args) {
		SpringApplication.run(GpmApiCallApplication.class, args);

	}
	
	@Override
	public void  run(String...args) throws Exception {
         
		String startDate ="2023-11-01 10:00:00";
		String  endDate = "2023-11-01 09:59:59";
		String dataSourceName ="EXPORTED ACTIVE ENERGY";
		
		// Goma
		String goma_SiteName = "KIVUE";
		String goma_MainMeter = "Main Meter";
		String goma_SolarMeter = "PV Generation Meter";
		String goma_Genset_1 = "Genset 1 Meter";
		String goma_Genset_2 = "Genset 2 Meter";

		
		// Tadu
		String tadu_SiteName = "Tadu";
		String tadu_MainMeter = "Meter Load";
		String tadu_SolarMeter = "Meter Solar PV";
		String tadu_Genset = "Meter Diesel Generator";
		
		// Faradje Site
		String faradje_SiteName = "Faradje";
		String faradje_MainMeter = "Meter Load";
		String faradje_SolarMeter = "Meter Solar PV";
		String faradje_Genset = "Meter Diesel Generator";

		
		System.out.println("\n-------------------------------------------");
		System.out.println("-----------------Goma Site -------------");
		System.out.println("-------------------------------------------\n");
		System.out.println(" {\n"
				+ "  Site :"+ goma_SiteName
				+","
				+ "\n"
				+ "  Date : "+ gpmPlusApiDataDownloader.getDateReport(goma_SiteName, goma_MainMeter, dataSourceName, startDate, endDate)
				+","
				+ "\n"
				+ "  kWh Main Meter: "+ gpmPlusApiDataDownloader.get_Energy_In_Kwh(goma_SiteName, goma_MainMeter, dataSourceName, startDate, endDate)
				+","
				+ "\n"
				+ "  kWh Solar : "+ gpmPlusApiDataDownloader.get_Energy_In_Kwh(goma_SiteName, goma_SolarMeter, dataSourceName, startDate, endDate)
				+","
				+ "\n"
				+ "  kWh Genset : "+ (gpmPlusApiDataDownloader.get_Energy_In_Kwh(goma_SiteName, goma_Genset_1, dataSourceName, startDate, endDate)+gpmPlusApiDataDownloader.get_Energy_In_Kwh(goma_SiteName, goma_Genset_2, dataSourceName, startDate, endDate))
				+"\n"
				+" }");
		
		System.out.println("\n-------------------------------------------");
		System.out.println("-----------------Tadu Site -------------");
		System.out.println("-------------------------------------------\n");
		System.out.println(" {\n"
				+ "  Site :"+ tadu_SiteName
				+","
				+ "\n"
				+ "  Date : "+ gpmPlusApiDataDownloader.getDateReport(tadu_SiteName, tadu_MainMeter, dataSourceName, startDate, endDate)
				+","
				+ "\n"
				+ "  kWh Main Meter: "+ gpmPlusApiDataDownloader.get_Energy_In_Kwh(tadu_SiteName, tadu_MainMeter, dataSourceName, startDate, endDate)
				+","
				+ "\n"
				+ "  kWh Solar : "+ gpmPlusApiDataDownloader.get_Energy_In_Kwh(tadu_SiteName, tadu_SolarMeter, dataSourceName, startDate, endDate)
				+","
				+ "\n"
				+ "  kWh Genset : "+ gpmPlusApiDataDownloader.get_Energy_In_Kwh(tadu_SiteName, tadu_Genset, dataSourceName, startDate, endDate)
				+"\n"
				+" }");
		
		System.out.println("\n-------------------------------------------");
		System.out.println("-----------------Faradje Site -------------");
		System.out.println("-------------------------------------------\n");
		System.out.println(" {\n"
				+ "  Site :"+ faradje_SiteName
				+","
				+ "\n"
				+ "  Date : "+ gpmPlusApiDataDownloader.getDateReport(faradje_SiteName, faradje_MainMeter, dataSourceName, startDate, endDate)
				+","
				+ "\n"
				+ "  kWh Main Meter: "+ gpmPlusApiDataDownloader.get_Energy_In_Kwh(faradje_SiteName, faradje_MainMeter, dataSourceName, startDate, endDate)
				+","
				+ "\n"
				+ "  kWh Solar : "+ gpmPlusApiDataDownloader.get_Energy_In_Kwh(faradje_SiteName, faradje_SolarMeter, dataSourceName, startDate, endDate)
				+","
				+ "\n"
				+ "  kWh Genset : "+ gpmPlusApiDataDownloader.get_Energy_In_Kwh(faradje_SiteName, faradje_Genset, dataSourceName, startDate, endDate)
				+"\n"
				+" }");
	}
	
}
