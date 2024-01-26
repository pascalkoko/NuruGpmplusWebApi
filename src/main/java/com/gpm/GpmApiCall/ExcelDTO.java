package com.gpm.GpmApiCall;

public class ExcelDTO {

	String date;
	String siteName;
	double kwh_MainMeter;
	double kwh_SolarMeter;
	double kwh_Genset1;
	double kwh_Genset2;
	double kwh_Genset;
	
	public ExcelDTO() {
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
	public double getKwh_Genset1() {
		return kwh_Genset1;
	}
	public void setKwh_Genset1(double kwh_Genset1) {
		this.kwh_Genset1 = kwh_Genset1;
	}
	public double getKwh_Genset2() {
		return kwh_Genset2;
	}
	public void setKwh_Genset2(double kwh_Genset2) {
		this.kwh_Genset2 = kwh_Genset2;
	}
	public double getKwh_Genset() {
		return kwh_Genset;
	}
	public void setKwh_Genset(double kwh_Genset) {
		this.kwh_Genset = kwh_Genset;
	}

}
