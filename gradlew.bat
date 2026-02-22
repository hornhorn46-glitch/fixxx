@echo off
set DIR=%~dp0
if not exist "%DIR%gradle\wrapper\gradle-wrapper.jar" (
  echo gradle-wrapper.jar is missing. Regenerate wrapper in Android Studio or run: gradle wrapper --gradle-version 8.5
  exit /b 1
)
"%JAVA_HOME%\bin\java.exe" -classpath "%DIR%gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
