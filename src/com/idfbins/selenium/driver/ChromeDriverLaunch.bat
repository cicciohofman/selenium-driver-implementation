title Running Chrome Driver...
@echo off
color 0a
java -Dwebdriver.chrome.driver=chromedriver.exe -jar selenium-server-standalone-3.6.0.jar -port 4444
pause