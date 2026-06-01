@echo off
REM Sample setenv.bat for KiotRetail development.
REM Place this in <TOMCAT_HOME>/bin/setenv.bat before starting Tomcat.
REM This file MUST stay outside the project repository (it contains credentials).
REM
REM See: docs/stories/HRS-001-secrets-out-of-webxml/

set DB_URL=jdbc:sqlserver://localhost:1433;databaseName=DBFinora;trustServerCertificate=true
set DB_USER=sa
set DB_PASSWORD=changeme

REM Optional Java heap tuning:
REM set CATALINA_OPTS=%CATALINA_OPTS% -Xms512m -Xmx1024m
