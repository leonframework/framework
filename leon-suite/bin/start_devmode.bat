
set base=%~dp0
call %base%\vars.bat

set LEON_DEPLOYMENT_MODE=development
call %maven%\bin\mvn.bat "jetty:run"
