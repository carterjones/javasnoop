#!/bin/bash

# Figure out if Linux/Mac OSX.
OS="`uname -a`"
ISMAC=0
CURJDK=

if [[ $OS == *Darwin* ]]
   then
   ISMAC=1
   echo [0] Detected Mac OSX
   cd /System/Library/Frameworks/JavaVM.framework/Versions
   MACJDK="`readlink CurrentJDK`"
   # test to see if CURJDK is 1.6+ if not, fall through
   if [[ $MACJDK == *1.6* ]]
      then
      CURJDK=$MACJDK
      echo [-] Error: CurrentJDK is not 1.6. Will check for other versions.
   fi
fi

#   if [ "$ISMAC" = "0" ]
#      then
      echo [0] Detected other Linux/Unix
#   fi

   if [ ! -z "$JDK_HOME" ]
      then
      echo [1] Found JDK_HOME environment variable. Using JDK at $JDK_HOME.
      CURJDK=$JDK_HOME
   elif [ ! -z "$JAVA_HOME" ]
      then
      echo [1] Found JAVA_HOME environment variable. Using JDK in $JAVA_HOME
      CURJDK=$JAVA_HOME
   else
      echo [1] Error: Neither JAVA_HOME or JDK_HOME environment variables were set to
      echo "    location of a JDK."
      exit -1
   fi

# Copy the tools.jar from the JDK to ./lib.

# copy $CURJDK\lib\tools.jar .\lib

# Turn off Java security.
rm -rf ~/.java.policy
copy resources/unsafe.policy ~/.java.policy

# Start JavaSnoop.

$CURJDK/bin/java -jar JavaSnoop.jar -Xmx128m

# Turn on Java security.

copy resources/safe.policy ~/.java.policy