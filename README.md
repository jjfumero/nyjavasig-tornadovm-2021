# NYJavaSIG 2021 Demos 

This repository contains examples and tests shown at the NYJavaSIG TornadoVM talk in Feb 2021

Link to the event: [link](https://www.eventbrite.com/e/transparent-heterogeneous-computing-for-java-via-tornadovm-tickets-138156215961)

Slides coming soon! 

### Pre-requisites and Setup

* Install TornadoVM. Full guideline [here](https://github.com/beehive-lab/TornadoVM/blob/master/INSTALL.md)
* OpenCL or PTX device:
  * If OpenCL is selected, installation with OpenCL >= 1.2 installed
  * If the PTX backend is selected, CUDA >= 10  
* Maven 3.6

Once TornadoVM is installed, please configure the `sources.sh` file with your paths to the `JAVA_HOME` and `TORNADO_ROOT`.

```bash
## Provide the paths to your path to the OpenJDK+JVMCI bin
export JAVA_HOME=<path-to-OpenJDK>
# Provide your path to the TornadoVM root folder
export TORNADO_ROOT=<path-to-TornadoVM>

## Keep these env-variables are they are
export PATH="${PATH}:${TORNADO_ROOT}/bin/bin/"
export TORNADO_SDK=${TORNADO_ROOT}/bin/sdk
export CLASSPATH=target/tornado-1.0-SNAPSHOT.jar
```

Run:
```bash
$ . sources.sh
```

### 1) Running DFT


### 2) Running on FPGAs

Note, you need an FPGA to run these demos. 


### 3) Running Live Task Migration 


### 4) Python 
