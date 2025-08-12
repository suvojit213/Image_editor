@REM
@REM Copyright 2012 the original author or authors.
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM      http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM

@if "%DEBUG%" == "" @echo off
@REM ##########################################################################
@REM
@REM  Gradle wrapper script for Windows
@REM
@REM ##########################################################################

@REM Set local scope for the variables with windows 9x compatibility
@setlocal

@REM Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS environment variables.
@set DEFAULT_JVM_OPTS="-Xmx64m -Xms64m"

@REM Find Java
@if defined JAVA_HOME goto findJavaFromJavaHome

@for %%i in (java.exe) do @if "%%~fi" neq "" @set JAVA_EXE=%%~fi
@if defined JAVA_EXE goto execute

@echo.
@echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
@echo.
@echo Please set the JAVA_HOME variable in your environment to match the location of your Java installation.
@goto fail

:findJavaFromJavaHome
@set JAVA_EXE="%JAVA_HOME%\bin\java.exe"
@if exist %JAVA_EXE% goto execute

@echo.
@echo ERROR: JAVA_HOME is set to an invalid directory. --^>
@echo JAVA_HOME = %JAVA_HOME%
@echo Please set the JAVA_HOME variable in your environment to match the location of your Java installation.
@goto fail

:execute
@set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

@REM Set "user.dir" to the parent of the wrapper directory
@set "APP_HOME=%~dp0"
@set "APP_HOME=%APP_HOME:~0,-1%"

@REM Execute Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% -cp "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

:end
@endlocal

:fail
@echo.
@pause
