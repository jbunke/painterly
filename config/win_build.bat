@echo off

setlocal

set "DEV=Jordan Bunke"
set "ID=painterly"
set "NAME=Painterly"

echo Building %NAME% for Windows...

set "ARTIFACT_DIR=..\out\artifacts\build_release"
set "OUTPUT_DIR=..\out\bin\win"

rd /s /q "%OUTPUT_DIR%\build"
rd /s /q "%OUTPUT_DIR%\runtime"
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"
:: if not exist "%OUTPUT_DIR%\installer" mkdir "%OUTPUT_DIR%\installer"

:: Analyze Java module dependencies
jdeps --multi-release 17 --print-module-deps "%ARTIFACT_DIR%\%ID%.jar" > "%OUTPUT_DIR%\modules.txt"
echo 1/4: Analyzed dependencies

:: Read modules from file
set "MODULES="
for /f "delims=" %%i in (%OUTPUT_DIR%\modules.txt) do set "MODULES=%%i"
:: set /p MODULES=<"%OUTPUT_DIR%\modules.txt"
echo Modules: %MODULES%

:: Generate runtime image
set "JAVA_MOD_PATH=C:\Program Files\Java\jdk-17\jmods"

jlink --module-path "%JAVA_MOD_PATH%" --add-modules "%MODULES%" --output "%OUTPUT_DIR%\runtime"
echo 2/4: Generated runtime image

set "VERSION_FILE=..\res\version"
set /p VERSION=<"%VERSION_FILE%"

set "RELEASE_FILE=..\res\release"
set /p RELEASE_MODE=<"%RELEASE_FILE%"

set "FILENAME_BASE=%ID%-%VERSION%"
if "%RELEASE_MODE%"=="demo" set "FILENAME_BASE=%FILENAME_BASE%-demo"

jpackage ^
    --type exe ^
    --input "%ARTIFACT_DIR%" ^
    --dest "%OUTPUT_DIR%\installer" ^
    --name "%NAME%" ^
    --app-version "%VERSION%" ^
    --main-jar "%ID%.jar" ^
    --runtime-image "%OUTPUT_DIR%\runtime" ^
    --vendor "%DEV%" ^
    --win-dir-chooser ^
    --win-shortcut ^
    --win-menu ^
    --win-menu-group "%NAME%" ^
    --description "%NAME%"
move "%OUTPUT_DIR%\installer\%NAME%-%VERSION%.exe" "%OUTPUT_DIR%\installer\%FILENAME_BASE%-installer.exe"

echo 3/4: Built Windows installer with jpackage

jpackage ^
    --type app-image ^
    --input "%ARTIFACT_DIR%" ^
    --dest "%OUTPUT_DIR%\build" ^
    --name "%ID%" ^
    --app-version "%VERSION%" ^
    --main-jar "%ID%.jar" ^
    --runtime-image "%OUTPUT_DIR%\runtime" ^
    --vendor "%DEV%" ^
    --description "%NAME%"

echo 4/4: Built Windows executable with jpackage

endlocal
