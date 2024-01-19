package com.gpm.GpmApiCall;

import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Component
@ConfigurationProperties("gpmplus")
@Validated
public class PropertiesGpmPlus {
	
	public static class GpmPlusWebApi{
		   
		@NotNull
		@URL
		private String url;
		@NotBlank
		private String username;
		@NotBlank
		private String password;
		@NotBlank
		private String accessToken;
		
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getAccessToken() {
			return accessToken;
		}
		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}
		
	 }
	
	
	public static class DataListParams{
		
		int aggregationType;
		String grouping;
		
		public int getAggregationType() {
			return aggregationType;
		}
		public void setAggregationType(int aggregationType) {
			this.aggregationType = aggregationType;
		}
		public String getGrouping() {
			return grouping;
		}
		public void setGrouping(String grouping) {
			this.grouping = grouping;
		}
		
	}
	
	
	@NotNull
	private GpmPlusWebApi gpmPlusWebApi;


	public GpmPlusWebApi getGpmPlusWebApi() {
		return gpmPlusWebApi;
	}


	public void setGpmPlusWebApi(GpmPlusWebApi gpmpluswebapi) {
		this.gpmPlusWebApi = gpmpluswebapi;
	}

	@NotNull
	private DataListParams dataListParams;


	public DataListParams getDataListParams() {
		return dataListParams;
	}
	public void setDataListParams(DataListParams dataListParams) {
		this.dataListParams = dataListParams;
	}



}
