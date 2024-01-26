package com.gpm.GpmApiCall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import org.json.simple.JSONObject;


public class Gpm_Authentication {

	private static final Logger LOG = LoggerFactory.getLogger(Gpm_Authentication.class);
	
	private PropertiesGpmPlus propertiesGpmPlus;
	private RestTemplate restTemplate;


	public Gpm_Authentication(PropertiesGpmPlus propertiesGpmPlus) {
		this.propertiesGpmPlus = propertiesGpmPlus;
		this.restTemplate = new RestTemplate();
	}
	
	
	public PropertiesGpmPlus getPropertiesGpmPlus() {
		return propertiesGpmPlus;
	}
	public void setPropertiesGpmPlus(PropertiesGpmPlus propertiesGpmPlus) {
		this.propertiesGpmPlus = propertiesGpmPlus;
	}

	 

	public Gpm_Authentication() {
	}


	/**
	 * @return "Bearer AADs17M......."
	 * @throws JSONException 
	 * @throws GpmPlusConfigurationException
	 */
	
	public String determineAuthorizationHeaderValue(){		
		
		String authenticationURL = this .propertiesGpmPlus.getAuthentication().getUrl()+"/api/Account/Token";
		String username = this.propertiesGpmPlus.getAuthentication().getUsername();
		String password = this.propertiesGpmPlus.getAuthentication().getPassword();
				
		//set headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HashMap< String, String> map = new HashMap<>();
		map.put("username", username);
		map.put("password", password);
		
		// build the request body
		JSONObject json = new JSONObject();
		json.putAll(map);
		
		HttpEntity<JSONObject>  request = new HttpEntity<JSONObject>(json,headers);
			
		ResponseEntity<TokenRet> tokenRetResponse = restTemplate.postForEntity(  
																authenticationURL, 
																request, 
																TokenRet.class);
	
		if (tokenRetResponse != null) {
			if (tokenRetResponse.getBody() != null) {
				TokenRet token = tokenRetResponse.getBody();
				
				// "bearer AQAAANCMnd8BFdERjHoA......."
				return token.getToken_type() + " " + token.getAccess_token();
				
			} else {
				throw new RuntimeException("GpmPlus didn't deliver a Bearer-token response body.");
			}
		} else {
			throw new RuntimeException("GpmPlus didn't deliver a Bearer-token response.");
		}
		
	}
}
