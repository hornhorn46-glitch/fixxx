#!/usr/bin/env sh
# Minimal Gradle wrapper launcher (requires gradle-wrapper.jar in gradle/wrapper).
# If missing, regenerate wrapper in Android Studio or run: gradle wrapper --gradle-version 8.5
DIR="$(cd "$(dirname "$0")" && pwd)"
JAVA_CMD="${JAVA_HOME:+$JAVA_HOME/bin/}java"
if [ ! -f "$DIR/gradle/wrapper/gradle-wrapper.jar" ]; then
  echo "gradle-wrapper.jar is missing. Regenerate wrapper (Android Studio: 'Add Gradle Wrapper') or run: gradle wrapper --gradle-version 8.5"
  exit 1
fi
exec "$JAVA_CMD" -classpath "$DIR/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
