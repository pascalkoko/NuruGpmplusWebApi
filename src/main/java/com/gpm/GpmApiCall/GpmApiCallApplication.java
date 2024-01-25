package com.gpm.GpmApiCall;

import java.util.Collection;

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
		
		 Collection<GpmExcelDTO> gpmExcelReports= gpmPlusApiDataDownloader.get_Gpm_Reports();
		
		for (GpmExcelDTO excelDTO : gpmExcelReports) {
			
			if (excelDTO.getDate()!= null && excelDTO.getSiteName() != null) {
				
				System.out.println("\n{\n"
						+ "  Date :"+ excelDTO.getDate()
						+","
						+ "\n"
						+ "  site : "+ excelDTO.getSiteName()
						+","
						+ "\n"
						+ "  kwh Main Meter : "+ excelDTO.getKwh_MainMeter()
						+","
						+ "\n"
						+ "  kwh Solar : "+ excelDTO.getKwh_SolarMeter()
						+","
						+ "\n"
						+ "  kwh Genset : "+ excelDTO.getKwh_Genset()
						+"\n"
						+" }");
			}
		}
         
	
	} 
	
}
