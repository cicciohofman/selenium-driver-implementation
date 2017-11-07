title Running Firefox Driver...
@echo off
color 0a
java -Dwebdriver.gecko.driver=.\geckodriver.exe -jar selenium-server-standalone-3.6.0.jar -port 4444
pause