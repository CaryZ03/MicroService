@echo off
set LOCALCLASSPATH=..\jprofiler.jar;..\..\.install4j\i4jruntime.jar
for %%i in (..\..\lib\*.jar) do call lcp.bat %%i
for %%i in (..\..\lib\probe\*.jar) do call lcp.bat %%i

java -Xmx512m -classpath "%LOCALCLASSPATH%" com.jprofiler.frontend.FrontendApplication
