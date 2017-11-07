package com.idfbins.selenium.driver;

public enum MobileEmulationDevices {
	
	BlackBerry_Z30 ("BlackBerry Z30"),
	Blackberry_PlayBook ("Blackberry PlayBook"),
	Galaxy_Note_3 ("Galaxy Note 3"),
	Galaxy_Note_II ("Galaxy Note II"),
	Galaxy_S_III ("Galaxy S III"),
	Galaxy_S5 ("Galaxy S5"),
	Kindle_Fire_HDX ("Kindle Fire HDX"),
	LG_Optimus_L70 ("LG Optimus L70"),
	Laptop_With_HiDPI_Screen ("Laptop with HiDPI screen"),
	Laptop_With_MDPI_Screen ("Laptop with MDPI screen"),
	Laptop_With_Touch ("Laptop with touch"),
	Microsoft_Lumia_550 ("Microsoft Lumia 550"),
	Microsoft_Lumia_950 ("Microsoft Lumia 950"),
	Nexus_10 ("Nexus 10"),
	Nexus_4 ("Nexus 4"),
	Nexus_5 ("Nexus 5"),
	Nexus_5X ("Nexus 5X"),
	Nexus_6 ("Nexus 6"),
	Nexus_6P ("Nexus 6P"),
	Nexus_7 ("Nexus 7"),
	Nokia_Lumia_520 ("Nokia Lumia 520"),
	Nokia_N9 ("Nokia N9"),
	iPad ("iPad"),
	iPad_Pro ("iPad Pro"),
	iPad_Mini ("iPad Mini"),
	iPhone_4 ("iPhone 4"),
	iPhone_5 ("iPhone 5"),
	iPhone_6 ("iPhone 6"),
	iPhone_6_Plus ("iPhone 6 Plus");
	
	private String value;
	
	private MobileEmulationDevices(String type){
		value = type;
	}
	
	public String getValue(){
		return value;
	}
	
	public MobileEmulationDevices valueOfName(final String name) {
		final String enumName = name.replaceAll(" ", "_");
		try {
			return valueOf(enumName);
		} catch (final IllegalArgumentException e) {
			return null;
		}
	}
}

