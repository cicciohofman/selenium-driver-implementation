package com.idfbins.selenium.driver;

public enum Browsers {
	Chrome("chrome"),
	Chrome_Mobile("chromeMobile"),
	InternetExplorer("ie"),
	Firefox("firefox"),
	Edge("edge");
	String value;
	
	private Browsers(String type){
		value = type;
	}
	
	public String getValue(){
		return value;
	}
	
	public static Browsers valueOfString(final String string) {
		String enumName = "";
		switch (string){
			case "chrome": 
				enumName = "Chrome";
				break;
			case "chromeMobile": 
				enumName = "Chrome_Mobile";
				break;
			case "ie": 
				enumName = "InternetExplorer";
				break;
			case "firefox": 
				enumName = "Firefox";
				break;
			case "edge": 
				enumName = "Edge";
				break;
		}
		try {
			return valueOf(enumName);
		} catch (final IllegalArgumentException e) {
			return null;
		}
	}
}
