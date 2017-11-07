package com.idfbins.selenium.driver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;

public class DriverImplementation {
	private WebDriver webDriver = null;
	private Browsers browser = null;
	private boolean useEmbeddedDriver = true;
	private String urlToUse = null;
	private String customProfilePathForDriver = null;
	private String cookieFileLocation;
	private int latencyInSecondsForDriver = 0;
	
	public DriverImplementation() throws Exception {
		
	}

	public void loadImplementation(Browsers localBrowserToUse, boolean localUseEmbeddedDriver, String localUrlToUse, MobileEmulationDevices localMobileEmulationDevice, String localCustomProfileNotCookies, String localCookieFileLocation, int localLatencyInSecondsForDriver) throws Exception {
		
		if(localBrowserToUse != null) {
			setBrowser(localBrowserToUse);
		} else {
			throw new Exception("Error: No browser set for Driver to load");
		}
		
		setUseEmbeddedDriver(localUseEmbeddedDriver);
		
		if(localUrlToUse != null) {
			setUrlToUse(localUrlToUse);
		} else {
			throw new Exception("Error: No URL set for Driver to load");
		}
		
		if(localCustomProfileNotCookies != null) {
			if(!getBrowser().equals(Browsers.Chrome)) {
				throw new Exception("Only Chrome is a valid option for using Custom Profiles or Cookies and Browser Proxy for Latency");
			} else {
				setCustomProfilePathForDriver(localCustomProfileNotCookies);
			}
		}
		
		if(localCookieFileLocation != null) {
			if(!getBrowser().equals(Browsers.Chrome)) {
				throw new Exception("Only Chrome is a valid option for using Custom Profiles or Cookies and Browser Proxy for Latency");
			} else {
				setCookieFileLocation(localCookieFileLocation);
			}
		}
		
		if(localLatencyInSecondsForDriver > 0) {
			if(!getBrowser().equals(Browsers.Chrome)) {
				throw new Exception("Only Chrome is a valid option for using Custom Profiles or Cookies and Browser Proxy for Latency");
			} else {
				setLatencyInSecondsForDriver(localLatencyInSecondsForDriver);
			}
		}
		
		if(isUseEmbeddedDriver()) {
			switch(getBrowser()) {
			case Chrome: case Chrome_Mobile:
				System.setProperty("webdriver.chrome.driver", exportResource("chromedriver.exe"));
				break;
			case InternetExplorer:
				System.setProperty("webdriver.ie.driver", exportResource("IEDriverServer.exe"));
				break;
			case Edge:
				System.setProperty("webdriver.edge.driver", exportResource("MicrosoftWebDriver.exe"));
				break;
			case Firefox:
				System.setProperty("webdriver.gecko.driver", exportResource("geckodriver.exe"));
				break;
			}
			
		}
	
		switch(getBrowser()) {
			case Chrome:
				ChromeOptions chromeOptions = new ChromeOptions();
				if (getCustomProfilePathForDriver() != null) {
					chromeOptions.addArguments("user-data-dir=" + getCustomProfilePathForDriver());
				}
				chromeOptions.addArguments("disable-popup-blocking");
				chromeOptions.addArguments("start-maximized");
				chromeOptions.addArguments("disable-infobars");
				if(getLatencyInSecondsForDriver() > 0) {
					BrowserMobProxy proxy = new BrowserMobProxyServer();
					proxy.start(0);
					proxy.setLatency(getLatencyInSecondsForDriver(), TimeUnit.SECONDS);
					Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
					chromeOptions.setCapability(CapabilityType.PROXY, seleniumProxy);
				}
				if(getCookieFileLocation() != null) {
					createDriverSessionCookieFile(getCookieFileLocation());
					setDriverSessionCookiesFromFile();
				}
				if(isUseEmbeddedDriver()) {
					setWebDriver(new ChromeDriver(chromeOptions));
				} else {
					setWebDriver(new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), chromeOptions));
				}
				if(getUrlToUse() != null) {
					if(!getUrlToUse().equals("")) {
						getWebDriver().get(getUrlToUse());
					}
				}
				break;
			case Chrome_Mobile:
				HashMap<String, String> mobileEmulation = new HashMap<String, String>();
				mobileEmulation.put("deviceName", MobileEmulationDevices.Nexus_5X.getValue()); 
				ChromeOptions chromeMobileOptions = new ChromeOptions();
				chromeMobileOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
				if(getLatencyInSecondsForDriver() > 0) {
					BrowserMobProxy proxy = new BrowserMobProxyServer();
					proxy.start(0);
					proxy.setLatency(getLatencyInSecondsForDriver(), TimeUnit.SECONDS);
					Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
					chromeMobileOptions.setCapability(CapabilityType.PROXY, seleniumProxy);
				}
				if(getCookieFileLocation() != null) {
					createDriverSessionCookieFile(getCookieFileLocation());
					setDriverSessionCookiesFromFile();
				}
				if(isUseEmbeddedDriver()) {
					setWebDriver(new ChromeDriver(chromeMobileOptions));
				} else {
					setWebDriver(new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), chromeMobileOptions));
				}
				if(getUrlToUse() != null) {
					if(!getUrlToUse().equals("")) {
						getWebDriver().get(getUrlToUse());
					}
				}
				break;
			case InternetExplorer:
				InternetExplorerOptions internetExplorerOptions = new InternetExplorerOptions();
				internetExplorerOptions.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
				if(isUseEmbeddedDriver()) {
					setWebDriver(new InternetExplorerDriver(internetExplorerOptions));
				} else {
					setWebDriver(new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), internetExplorerOptions));
				}
				if(getUrlToUse() != null) {
					if(!getUrlToUse().equals("")) {
						getWebDriver().get(getUrlToUse());
					}
				}
				break;
			case Edge:
				EdgeOptions edgeOptions = new EdgeOptions();
				if(isUseEmbeddedDriver()) {
					setWebDriver(new EdgeDriver(edgeOptions));
				} else {
					setWebDriver(new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), edgeOptions));
				}
				if(getUrlToUse() != null) {
					if(!getUrlToUse().equals("")) {
						getWebDriver().get(getUrlToUse());
					}
				}
				break;
			case Firefox:
				FirefoxOptions firefoxOptions = new FirefoxOptions();
				firefoxOptions.setCapability("marionette", true);
				if(getLatencyInSecondsForDriver() > 0) {
					BrowserMobProxy proxy = new BrowserMobProxyServer();
					proxy.start(0);
					proxy.setLatency(getLatencyInSecondsForDriver(), TimeUnit.SECONDS);
					Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
					firefoxOptions.setCapability(CapabilityType.PROXY, seleniumProxy);
				}
				if(isUseEmbeddedDriver()) {
					setWebDriver(new FirefoxDriver(firefoxOptions));
				} else {
					setWebDriver(new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), firefoxOptions));
				}
				if(getUrlToUse() != null) {
					if(!getUrlToUse().equals("")) {
						getWebDriver().get(getUrlToUse());
					}
				}
				break;
		}
	}

	public String exportResource(String resourceName) throws Exception {
		InputStream stream = null;
		OutputStream resStreamOut = null;
		String jarFolder;
		try {
			stream = DriverImplementation.class.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
			if(stream == null) {
				throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
			}
			jarFolder = new File(DriverImplementation.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
			File f = new File(jarFolder + "/" + resourceName);
			if(!f.exists()) {
				int readBytes;
				byte[] buffer = new byte[4096];

				resStreamOut = new FileOutputStream(jarFolder + "/" + resourceName);
				while ((readBytes = stream.read(buffer)) > 0) {
					resStreamOut.write(buffer, 0, readBytes);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			stream.close();
			if(resStreamOut != null){
				resStreamOut.close();
			}
		}
		if(resStreamOut != null){
			System.out.println("Driver successfully extracted to: " + jarFolder + "/" + resourceName);
		}else{
			System.out.println("Driver already exists at: " + jarFolder + "/" + resourceName);
		}
		return jarFolder + "/" + resourceName;
	}

	public WebDriver getWebDriver() {
		WebDriver toReturn = webDriver;
		if (!getBrowser().equals(Browsers.Chrome)) {
			toReturn.manage().window().maximize();
		}
		return toReturn;
	}
	
	private void setWebDriver(WebDriver webDriver) {
		this.webDriver = webDriver;
	}
	
	public Browsers getBrowser() {
		return browser;
	}

	private void setBrowser(Browsers browser) {
		this.browser = browser;
	}
	
	public boolean isUseEmbeddedDriver() {
		return useEmbeddedDriver;
	}

	public void setUseEmbeddedDriver(boolean useEmbeddedDriver) {
		this.useEmbeddedDriver = useEmbeddedDriver;
	}

	public String getUrlToUse() {
		return urlToUse;
	}

	private void setUrlToUse(String urlToUse) {
		this.urlToUse = urlToUse;
	}
	
	public void loadUrlInBrowser(String urlToUse) {
		setUrlToUse(urlToUse);
		getWebDriver().get(getUrlToUse());
	}

	public String getCustomProfilePathForDriver() {
		return customProfilePathForDriver;
	}

	private void setCustomProfilePathForDriver(String customProfilePathForDriver) {
		this.customProfilePathForDriver = customProfilePathForDriver;
	}
	
	public String getCookieFileLocation() {
		return cookieFileLocation;
	}

	private void setCookieFileLocation(String cookieFileLocation) {
		this.cookieFileLocation = cookieFileLocation;
	}
	
	private void createDriverSessionCookieFile(String fileNamePath) {
		File file = new File(fileNamePath);							
		try {		
			file.delete();
			file.createNewFile();
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			for(Cookie seleniumCookie : getWebDriver().manage().getCookies()) {
				bufferedWriter.write((seleniumCookie.getName() + ";" + seleniumCookie.getValue() + ";" + seleniumCookie.getDomain() + ";" + seleniumCookie.getPath() + ";" + seleniumCookie.getExpiry() + ";" + seleniumCookie.isSecure()));
				bufferedWriter.newLine();
			}
			bufferedWriter.flush();
			bufferedWriter.close();
			fileWriter.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private void setDriverSessionCookiesFromFile() {
		if (getCookieFileLocation() != null) {
			try {
				File file = new File(getCookieFileLocation());
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String strline;
				while ((strline = bufferedReader.readLine()) != null) {
					StringTokenizer token = new StringTokenizer (strline, ";");
					while (token.hasMoreTokens()) {
						String name = token.nextToken();
						String value = token.nextToken();
						String domain = token.nextToken();
						String path = token.nextToken();
						Date expiry = null;

						String possibleExpDateValue;
						if(!(possibleExpDateValue = token.nextToken()).equals("null")) {
							try {
								SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
								expiry = format.parse(possibleExpDateValue);
							} catch(ParseException pe) {
								System.err.println("Error parsing the passed in date.");
							}
						}
						Boolean isSecure = new Boolean(token.nextToken()).booleanValue();
						Cookie seleniumCookie = new Cookie(name,value,domain,path,expiry,isSecure);
						getWebDriver().manage().addCookie(seleniumCookie);
					}
				}
				bufferedReader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public int getLatencyInSecondsForDriver() {
		return latencyInSecondsForDriver;
	}

	private void setLatencyInSecondsForDriver(int latencyInSecondsForDriver) {
		this.latencyInSecondsForDriver = latencyInSecondsForDriver;
	}
	
	public void quit() {
		if (getWebDriver() != null){
			getWebDriver().quit();
		}
		
		try {
			System.clearProperty("config.browser");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			System.clearProperty("config.useEmbeddedDriver");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			System.clearProperty("config.urlToUse");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			System.clearProperty("config.customProfilePathForDriver");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			System.clearProperty("config.cookieFileLocation");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			System.clearProperty("config.latencyInSecondsForDriver");
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	/** This is the basic Fluent Wait method used in all calls below. By default, this method will wait for a maximum
	 * amount of time set by the user. It will poll the DOM every 5 milliseconds looking for the element, and the state,
	 * declared in other methods. It will also ignore Stale Element and No Such Element Exceptions while waiting.
	 * @param millisecondsToTimeout - The maximum time to wait for an element to be in the state specified in other methods.
	 * @return WebDriver Wait - The wait, with the specified arguments necessary to locate a WebElement.
	 */
	private Wait<WebDriver> wait(int millisecondsToTimeout) {
		FluentWait<WebDriver> wait =  new FluentWait<WebDriver>(getWebDriver())
				.withTimeout(millisecondsToTimeout, TimeUnit.MILLISECONDS)
				.pollingEvery(5, TimeUnit.MILLISECONDS)
				.ignoring(StaleElementReferenceException.class, NoSuchElementException.class);
		return wait;
	}
	
	public void delay(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void refreshPage() {
		getWebDriver().navigate().refresh();
		delay(500);
	}
	
	/** This method will perform a 'send keys' call, without needing to specify a WebElement to interact with.
	 * @param keysToSend - Selenium Keys - The Keys sequence you want to send.
	 */
	public void sendArbitraryKeys(Keys keysToSend) {
		new Actions(getWebDriver()).sendKeys(keysToSend).perform();
	}

	/** This method will perform a 'send keys' call, without needing to specify a WebElement to interact with.
	 * @param keysToSend - String Keys - The String character sequence you want to send.
	 */
	public void sendArbitraryKeys(String keysToSend) {
		new Actions(getWebDriver()).sendKeys(keysToSend).perform();
	}
	
	/** This method will perform a 'send keys' call to a specified WebElement, performing a 'control + all'
	 * function along the way, effectively clearing out any previously existing text.
	 * @param textBox - WebElement input box that you want to perform your action on.
	 * @param text - String text that you want to input into your text box.
	 */
	public void setText(WebElement textBox, String text) {
		clickWhenClickable(textBox);
		textBox.sendKeys(Keys.chord(Keys.CONTROL + "a"));
		textBox.sendKeys(text);
		textBox.sendKeys(Keys.TAB);
	}
	
	/** This method waits until the element passed in is click-able. It will check for the element, waiting 1 second
	 * and then attempting to re-get the element from the DOM. It will loop 15 times to do this.
	 * @param element - WebELement that you are waiting for.
	 * @return WebElement - The verified available WebElement.
	 */
	public WebElement waitUntilElementIsClickable(WebElement element) {
		int attempts = 0;
        while(attempts < 15) {
            try {
            	wait(1000).until(ExpectedConditions.elementToBeClickable(element));
        		break;
            } catch(TimeoutException | StaleElementReferenceException | NoSuchElementException e) {//Had to remove ElementNotFoundException
                element = getWebDriver().findElement(By.xpath(getXpathFromElement(element)));
            }
            attempts++;
        }
        return element;
	}
	
	/** This method waits until the element passed in is click-able. It will check for the element, waiting up to the
	 * amount of time specified (in milliseconds) and polling the DOM every 5 milliseconds.
	 * @param element - WebELement that you are waiting for.
	 * @param millisecondsToTimeout - maximum amount of time you want to wait for the element.
	 * @return WebElement - The verified available WebElement.
	 */
	public WebElement waitUntilElementIsClickable(WebElement element, int millisecondsToTimeout) {
		wait(millisecondsToTimeout).until(ExpectedConditions.elementToBeClickable(element));
        return element;
	}
	

	/** This method clicks a passed in element as soon as it is available in the DOM.
	 * @param elementToClick - WebELement that you are waiting for.
	 */
	public void clickWhenClickable(WebElement elementToClick) {
		delay(10);
		elementToClick = waitUntilElementIsClickable(elementToClick);
		delay(90);
		elementToClick.click();
	}
	
	/** This method clicks a passed in element as soon as it is available in the DOM,
	 * waiting up to a user configurable amount of time.
	 * @param elementToClick - WebELement that you are waiting for.
	 * @param millisecondsToTimeout - maximum amount of time you want to wait for the element.
	 */
	public void clickWhenClickable(WebElement elementToClick, int millisecondsToTimeout) {
		delay(10);
		elementToClick = waitUntilElementIsClickable(elementToClick, millisecondsToTimeout);
		delay(90);
		elementToClick.click();
	}
	
	/** This method clicks the first specified element until the second element becomes available,
	 * attempting up to 25 times.
	 * @param first - WebElement first element to click
	 * @param second - WebElement second element to look for while clicking the first element.
	 */
	public void clickFirstElementUntilSecondElementIsDisplayed(WebElement first, WebElement second) {
		first = waitUntilElementIsClickable(first);
		int i = 0;
		boolean isFound;
		do {
			isFound = true;
			first.click();
			i++;
			try {
				second.isDisplayed();
			} catch (Exception e) {
				isFound = false;
			}
		} while (!isFound && i < 25);
		
	}
	
	/** This method simply hovers over an element. This is useful when working with cascading menus.
	 * @param element - WebElement to hover over.
	 */
	public void hoverOver(WebElement element) {
		element = waitUntilElementIsVisible(element);
		Actions mouse = new Actions(getWebDriver());
		mouse.moveToElement(element);
		mouse.build().perform();
	}
	
	/** This method hovers over an element and then clicks that element to lock in a selection.
	 * @param element - WebElement to hover over and click.
	 */
	public void hoverOverAndClick(WebElement element) {
		element = waitUntilElementIsVisible(element);
		Actions mouse = new Actions(getWebDriver());
		mouse.moveToElement(element);
		mouse.click();
		mouse.build().perform();
	}
	
	/**
	 * Hovers over the first element until the second shows up.
	 * 
	 * @param first The first element to hover over.
	 * @param second The second element.
	 * @return Whether this method succeeded in under 25 tries.
	 */
	public boolean hoverOverFirstToShowSecond(WebElement first, WebElement second) {
		boolean passed = false;
		int index = 0;
		do {
			index++;
			hoverOver(first);
			delay(600);
			try {
				hoverOver(second);
				passed = true;
			} catch (Exception e) {
			}
		} while (!passed && index < 25);
		return passed;
	}

	/**
	 * Hovers over the first element until the second shows up. Use this if the second element doesn't exist until the first one is hovered over.
	 * 
	 * @param first The first element to hover over.
	 * @param secondXPath The xPath of the second.
	 * @return Whether this method succeeded in under 25 tries.
	 */
	public boolean hoverOverFirstToShowSecond(WebElement first, String secondXPath) {
		WebElement second;
		int index = 0;
		boolean passed = false;
		do {
			index++;
			hoverOver(first);
			delay(600);
			try {
				second = getWebDriver().findElement(By.xpath(secondXPath));
				hoverOver(second);
				passed = true;
			} catch (Exception e) {
			}
		} while (!passed && index < 25);
		return passed;
	}

	/**
	 * Hovers over the first element to click the second.
	 * 
	 * @param first The first element to hover over.
	 * @param second The second element.
	 * @return Whether this method succeeded in clicking the second element.
	 */
	public boolean hoverOverFirstToClickSecond(WebElement first, WebElement second) {
		if (hoverOverFirstToShowSecond(first, second)) {
			clickWhenClickable(second);
			return true;
		}
		return false;
	}

	/**
	 * Hovers over the first element to click the second. Use this if the second element doesn't exist until the first one is hovered over.
	 * 
	 * @param first The first element to hover over.
	 * @param secondXPath The xPath of the second.
	 * @return Whether this method succeeded in clicking the second element.
	 */
	public boolean hoverOverFirstToClickSecond(WebElement first, String secondXPath) {
		if (hoverOverFirstToShowSecond(first, secondXPath)) {
			WebElement second = getWebDriver().findElement(By.xpath(secondXPath));
			clickWhenClickable(second);
			return true;
		}
		return false;
	}
	
	/** This method selects the item in a drop-down list that at least partially matches the text passed in.
	 * @param dropDownList - WebElement drop-down list element.
	 * @param partialText - String text to search for int the list.
	 */
	public void selectFromDropDownByPartialText(WebElement dropDownList, String partialText) {
		List<WebElement> listOptions = new Select(dropDownList).getOptions();
		Boolean found = false;
		int i = 0;
		while(!found) {
			if (listOptions.get(i).getText().contains(partialText)) {
				found = true;
				new Select(dropDownList).selectByVisibleText(listOptions.get(i).getText());
			}
			i++;
		}
	}
	
	/** This method will select the OK or Cancel choice passed in on a modal confirmation pop-up window.
	 * @param ok - boolean true if ok, false if cancel
	 * @return - String inner text of the modal pop-up window.
	 */
	public String selectOKOrCancelFromPopup(boolean ok) {
		String popUpTextContents = "";
		Alert alert = getWebDriver().switchTo().alert();
		popUpTextContents = alert.getText();
		if(ok) {
			alert.accept();
		} else {
			alert.dismiss();
		}
		
		return popUpTextContents;
	}	
	
	/** This method will invoke a javascript call on the page to execute the javascript passed in to the method.
	 * @param javascriptString - String javascript call to execute.
	 */
	public void executeJavaScript(String javascriptString) {
		JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
		js.executeScript(javascriptString);
	}
	
	/** This method will use javascript to scroll the page to the element specified.
	 * @param element - WebElement element you want to scroll to.
	 */
	public void scrollToElement(WebElement element) {
		((JavascriptExecutor) getWebDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
	}
	
	/** This method takes a WebElement passed in and extracts the xPath that was originally used to create that WebElement.
	 * @param element - WebElement - element to use to extract the xPath.
	 * @return String xPath from the original WebElement.
	 */
	public static String getXpathFromElement(WebElement element) {
		String[] xpathSplit = element.toString().split("-> xpath: ");
		String xPath = "";
		if (xpathSplit.length == 1) {
			xpathSplit = element.toString().split("By.xpath: ");
			if (xpathSplit.length == 1) {
				System.out.println("The Element passed in is a proxy WebElement. As such, the original xPath cannot be extracted.");
			}
		}
		for (int i = 1; i < xpathSplit.length; i++) {
			String sanitizedXPath = xpathSplit[i];
			if (sanitizedXPath.startsWith(".")) {
				sanitizedXPath = sanitizedXPath.substring(1, sanitizedXPath.length());
			}
			if (i == xpathSplit.length - 1) {
				xPath += sanitizedXPath.substring(0, sanitizedXPath.length()-1);
			} else {
				xPath += sanitizedXPath.substring(0, sanitizedXPath.length()-3);
			}
		}
		return xPath;
	}
	
	/** This method uses javascript to encapsulate the passed-in WebElement in a flashing red box. This is useful
	 * for displaying what element is being accessed in the UI in real-time.
	 * @param element - WebElement to highlight.
	 */
	public void highlightElement(WebElement element) {
		for (int i = 0; i < 2; i++) {
			JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
			js.executeScript(
					"arguments[0].setAttribute('style', arguments[1]);",
					element, "color: red; border: 5px solid red;");
			js.executeScript(
					"arguments[0].setAttribute('style', arguments[1]);",
					element, "");
		}
	}
	
	/** This method waits until the element passed in is visible. It will check for the element, waiting 1 second
	 * and then attempting to re-get the element from the DOM. It will loop 15 times to do this.
	 * @param element - WebELement that you are waiting for.
	 * @return WebElement - The verified available WebElement.
	 */
	public WebElement waitUntilElementIsVisible(WebElement element) {
		int attempts = 0;
        while(attempts < 15) {
            try {
            	wait(1000).until(ExpectedConditions.visibilityOf(element));
        		break;
            } catch(TimeoutException | StaleElementReferenceException | NoSuchElementException e) {//Had to remove ElementNotFoundException
                element = getWebDriver().findElement(By.xpath(getXpathFromElement(element)));
            }
            attempts++;
        }
        return element;
	}
	
	/** This method waits until the element passed in is visible. It will check for the element, waiting up to the
	 * amount of time specified (in milliseconds) and polling the DOM every 5 milliseconds.
	 * @param element - WebELement that you are waiting for.
	 * @param millisecondsToTimeout - maximum amount of time you want to wait for the element.
	 * @return WebElement - The verified available WebElement.
	 */
	public WebElement waitUntilElementIsVisible(WebElement element, int millisecondsToTimeout) {
		int time = 0;
		if (millisecondsToTimeout < 1000) {
			wait(millisecondsToTimeout).until(ExpectedConditions.visibilityOf(element));
		} else {
	        while(time < millisecondsToTimeout) {
	            try {
	            	wait(1000).until(ExpectedConditions.visibilityOf(element));
	        		break;
	            } catch(TimeoutException | StaleElementReferenceException | NoSuchElementException e) {//Had to remove ElementNotFoundException
	                element = getWebDriver().findElement(By.xpath(getXpathFromElement(element)));
	            }
	            time = time + 1000;
	        }
		}
        return element;
	}
	
	/** This method waits a maximum of 15 seconds (15000 milliseconds) for an element to no longer be
	 * attached to the DOM.
	 * @param element - WebELement that you are waiting for.
	 */
	public void waitUntilElementIsNotVisible(WebElement element) {
		int attempts = 0;
		boolean found = true;
        while (attempts < 15) {
        	if (!checkIfElementExists(element, 1000)) {
        		found = false;
        		break;
        	} else {
        		wait(1000);
        	}
        	attempts ++;
        }
        if (found) {
			Assert.fail("The Element was still on the page after waiting for 15 seconds.");
		}
	}
	
	/** This method waits until the element passed in is no longer visible. It will check for the element, waiting up to the
	 * amount of time specified (in milliseconds) and polling the DOM every 5 milliseconds.
	 * @param element - WebELement that you are waiting for.
	 * @param millisecondsToTimeout - maximum amount of time you want to wait for the element.
	 * @return WebElement - The verified available WebElement.
	 */
	public void waitUntilElementIsNotVisible(WebElement element, int millisecondsToTimeout){
		int time = 0;
		boolean found = true;
		if (millisecondsToTimeout < 1000) {
			if (!checkIfElementExists(element, millisecondsToTimeout)) {
				found = false;
			}
		} else {
	        while(time < millisecondsToTimeout) {
	        	if (!checkIfElementExists(element, 500)) {
	        		found = false;
	        		break;
	        	} else {
	        		wait(500);
	        	}
	            time = time + 500;
	        }
		}
		if (found) {
			Assert.fail("The Element was still on the page after waiting for " + millisecondsToTimeout + " milliseconds.");
		}
	}
	
	/** This method clicks a passed in element as soon as it is available in the DOM.
	 * @param elementToClick - WebELement that you are waiting for.
	 */
	public void clickWhenVisible(WebElement elementToClick) {
		elementToClick = waitUntilElementIsVisible(elementToClick);
		elementToClick.click();
	}
	
	/** This method clicks a passed in element as soon as it is available in the DOM,
	 * waiting up to a user configurable amount of time.
	 * @param elementToClick - WebELement that you are waiting for.
	 * @param millisecondsToTimeout - maximum amount of time you want to wait for the element.
	 */
	public void clickWhenVisible(WebElement elementToClick, int millisecondsToTimeout) {
		elementToClick = waitUntilElementIsVisible(elementToClick, millisecondsToTimeout);
		elementToClick.click();
	}
	
	/** This method clicks on a passed in element every 100 milliseconds until it is no longer
	 * attached to the DOM.
	 * @param elementToClick - WebELement that you are waiting for.
	 */
	public void clickUntilNotVisible(WebElement elementToClick) {
		elementToClick = waitUntilElementIsVisible(elementToClick);
		while (checkIfElementExists(elementToClick, 100)) {
			elementToClick.click();
		}
	}
	
	/** This method will check to see if the element passed in is available in the DOM. It will
	 * wait up to the user configurable amount of time and then return true if found, or false
	 * if not found.
	 * @param element - WebELement that you are waiting for.
	 * @param checkTime - The maximum amount of time you want to wait for the element.
	 * @return - boolean - true if found, false if not found.
	 */
	public boolean checkIfElementExists(WebElement element, int checkTime) {
		try {
			//System.out.println("Trying to check if element is visible");
			waitUntilElementIsVisible(element, checkTime);
		} catch (Exception e) {
			//System.out.println("Caught Exception");
			//e.printStackTrace();
			return false;
		}
		//System.out.println("Found Element");
		return true;
	}
	
	/** This method will check to see if the element xPath String passed in is available in the DOM. It will
	 * wait up to the user configurable amount of time and then return true if found, or false
	 * if not found.
	 * @param element - WebELement that you are waiting for.
	 * @param checkTime - The maximum amount of time you want to wait for the element.
	 * @return - boolean - true if found, false if not found.
	 */
	public boolean checkIfElementExists(String xpath, int checkTime) {
		checkTime = (checkTime / 1000);
		int doesExist = getWebDriver().findElements(By.xpath(xpath)).size();
		boolean found = false;
		if(doesExist > 0) {
			found = true;
		} else {
			found = false;
		}
		int i = 0;
		while(!found && i < checkTime) {
			delay(10);
			delay(1000);
			doesExist = getWebDriver().findElements(By.xpath(xpath)).size();
			if(doesExist > 0) {
				found = true;
			} else {
				found = false;
			}
			
			i++;
		}
		return found;
	}
	
}
