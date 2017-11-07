title Running IE DriverServer...
@echo off
color 0a
java -Dwebdriver.ie.driver=.\IEDriverServer.exe -jar selenium-server-standalone-3.6.0.jar -port 4444
pause