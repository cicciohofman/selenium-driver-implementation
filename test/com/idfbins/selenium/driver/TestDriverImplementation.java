package com.idfbins.selenium.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TestDriverImplementation {
	
	private static void testGoogleSearch(DriverImplementation ourDriver) {
		WebDriver driver = ourDriver.getWebDriver();
		WebElement searchBox = driver.findElement(By.name("q"));
		searchBox.sendKeys("ChromeDriver");
		searchBox.submit();
	}

	public static void main(String[] args) throws Exception {
		DriverImplementation myDriverImplementation1 = null;
		try {
			myDriverImplementation1 = new DriverImplementation();
			myDriverImplementation1.loadImplementation(Browsers.Chrome, true, "http://google.com", null, null, null, 0);
			testGoogleSearch(myDriverImplementation1);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			myDriverImplementation1.quit();
		}
		
		DriverImplementation myDriverImplementation2 = null;
		try {
			myDriverImplementation2 = new DriverImplementation();
			myDriverImplementation2.loadImplementation(Browsers.Chrome, false, "http://google.com", null, null, null, 0);
			testGoogleSearch(myDriverImplementation2);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			myDriverImplementation2.quit();
		}
		
		
	}
	
}
