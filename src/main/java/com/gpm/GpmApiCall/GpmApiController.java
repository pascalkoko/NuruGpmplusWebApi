package com.gpm.GpmApiCall;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/gpm/api")
public class GpmApiController {
    
	private GpmPlusApiDataDownloader gpmPlusApiDataDownloader;
	
	public GpmApiController(GpmPlusApiDataDownloader gpmPlusApiDataDownloader) {
		this.gpmPlusApiDataDownloader = gpmPlusApiDataDownloader;
	}

	@GetMapping("/plants") 
	public   Plant[] getAllPlants(){
		return gpmPlusApiDataDownloader.fetchPlants();
	}

	
	
	@GetMapping("/plant/element") 
	public   Element[] getPlantElements(){
		return gpmPlusApiDataDownloader.fetchPlantElements("Tadu");
	}
	
	@GetMapping("/plant/element/datasource") 
	public   DataSource[] getPlantElementsDatasources(){
		return gpmPlusApiDataDownloader.fetchElementDataSources("KIVUE", "Main Meter");
	}
	
	
	@GetMapping("/datalist") 
	public   DataList[] getDataList(){
		return gpmPlusApiDataDownloader.fetchDataList("KIVUE", "PV Generation Meter", "EXPORTED ACTIVE ENERGY", "2023-11-01 10:00:00", "2023-11-01 09:59:59");
	}
}