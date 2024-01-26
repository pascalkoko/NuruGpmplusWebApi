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

		 Collection<ExcelDTO> gpmReports= gpmPlusApiDataDownloader.build_GPM_Report();
		
		for (ExcelDTO excelDTO : gpmReports) {
				
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
						+ "  kwh Genset_1 : "+ excelDTO.getKwh_Genset1()
						+","
						+ "\n"
						+ "  kwh Genset_2 : "+ excelDTO.getKwh_Genset2()
						+","
						+ "\n"
						+ "  kwh TotalGenset : "+ (excelDTO.getKwh_Genset1() + excelDTO.getKwh_Genset2())
						+"\n"
						+" }");
			
		}
        
		
	
	} 
	
}
