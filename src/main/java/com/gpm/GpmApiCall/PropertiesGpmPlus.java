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
	
	public static class Authentication{
		   
		@NotNull
		@URL
		private String url;
		@NotBlank
		private String username;
		@NotBlank
		private String password;
		
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
	 }
	
	
	
	@NotNull
	private Authentication authentication;


	public Authentication getAuthentication() {
		return authentication;
	}
	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}
}
