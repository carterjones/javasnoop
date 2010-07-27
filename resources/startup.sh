#!/bin/bash

# Figure out if Linux/Mac OSX.

CURJDK=
JAVA_POLICY=~/.java.policy
TOOLSJAR="/lib/tools.jar"
JAVABIN="bin/java"

# Who are we?
OSTYPE=`uname`
case ${OSTYPE} in

Darwin)
	echo [0] Detected Mac OSX
	JAVA_HOME=`/usr/libexec/java_home`
	if [[ $JAVA_HOME == *1.6* ]]
		then
		CURJDK="${JAVA_HOME}"
		# We love Mac
		TOOLSJAR="../Classes/classes.jar"
		[ -f lib/tools.jar ] && rm lib/tools.jar
	else
		echo [-] Error: CurrentJDK is not 1.6. Bailing
		exit 1
	fi
        echo [1] Found Java 1.6 at $JAVA_HOME.
	;;
Linux)
        echo [0] Detected Linux
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
		exit 1
	fi
	;;
*)
	;;
esac

# Copy the tools.jar / classes.jar from the JDK to ./lib.
cp ${CURJDK}/${TOOLSJAR} ./lib/

echo [2] Turning off Java security for JavaSnoop usage
# Turn off Java security.
if [ -f "${JAVA_POLICY}" ]
then
	cp ${JAVA_POLICY} ${JAVA_POLICY}.orig
	rm ${JAVA_POLICY}
else
	# Copy safe policy as orig
	cp resources/safe.policy ${JAVA_POLICY}.orig
fi
cp resources/unsafe.policy ${JAVA_POLICY}

echo [3] Starting JavaSnoop...
# Start JavaSnoop.
"${CURJDK}/${JAVABIN}" -jar JavaSnoop.jar -Xmx128m

echo [4] Turning Java security back on for safe browsing
# Undo what we did
cp ${JAVA_POLICY}.orig ${JAVA_POLICY}
