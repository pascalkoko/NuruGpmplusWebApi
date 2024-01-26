package com.gpm.GpmApiCall;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenRet {

	@JsonProperty("TokenType")
	private String token_type;
	@JsonProperty("AccessToken")
	private String access_token;
	
	public String getToken_type() {
		return token_type;
	}
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	@Override
	public String toString() {
		return "TokenRet [token_type =" + token_type + ", Access_Token =" + access_token + "]";
	}
}
