
## Provide the paths to your path to the OpenJDK+JVMCI bin
export JAVA_HOME=/home/juan/manchester/oracleGraal/openjdk1.8.0_262-jvmci-20.2-b03
# Provide your path to the TornadoVM root folder
export TORNADO_ROOT=/home/juan/manchester/tornado/tornado

## Keep these env-variables are they are
export PATH="${PATH}:${TORNADO_ROOT}/bin/bin/"
export TORNADO_SDK=${TORNADO_ROOT}/bin/sdk
export CLASSPATH=target/tornado-1.0-SNAPSHOT.jar

