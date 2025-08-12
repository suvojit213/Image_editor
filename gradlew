#!/usr/bin/env sh

##############################################################################
##
##  Gradle wrapper script for UNIX
##
##############################################################################

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    JAVA_CMD="$JAVA_HOME/bin/java"
else
    JAVA_CMD="java"
fi

# Determine the script path
SCRIPT_DIR=$(dirname "$0")

# Determine the wrapper jar path
WRAPPER_JAR="$SCRIPT_DIR/gradle/wrapper/gradle-wrapper.jar"

# Execute the wrapper
exec "$JAVA_CMD" -jar "$WRAPPER_JAR" "$@"
