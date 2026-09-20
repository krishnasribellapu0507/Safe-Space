@echo off
setlocal
set "APP=%~dp0..\web-app\index.html"
set "URL=file:///%APP:\=/%"
where msedge >nul 2>&1
if %errorlevel%==0 (
  start "Safe Space" msedge --app="%URL%" --start-maximized
  exit /b
)
where chrome >nul 2>&1
if %errorlevel%==0 (
  start "Safe Space" chrome --app="%URL%" --start-maximized
  exit /b
)
start "Safe Space" "%APP%"
