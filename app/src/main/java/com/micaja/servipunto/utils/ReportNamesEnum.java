package com.micaja.servipunto.utils;

public enum ReportNamesEnum {

	REPORT1(""), REPORT2(""), REPORT3(""), REPORT4(""), REPORT5(""), REPORT6(""), REPORT7(""), REPORT8(""), REPORT9("");
	
	private String reportName;
	
	private ReportNamesEnum(String reportName)
	{
		this.reportName	= reportName;
	}

	public String getReportName()
	{
		return reportName;
	}
}
