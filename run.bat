@echo off
::Windows script for running the Kahoot hack
::This script might be broken, the only thing I have for testing is a WINE terminal.

SET loc=%~dp0
SET builder=%loc%gradlew.bat

if exist build\libs\kahoot-hack.jar (
	::Kahoot hack is built, run.
	start java -jar %loc%build\libs\kahoot-hack.jar

) else (
	::Kahoot hack hasn't been built yet, build automatically and run.
	start builder
	start java -jar %loc%build\libs\kahoot-hack.jar
)

