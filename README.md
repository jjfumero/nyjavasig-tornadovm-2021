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

All these examples and scripts uses Java 8. For JDK >= 11, please file an issue, and we can work through it together.   

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
. sources.sh
```

Compile this tutorial:

```bash
mvn clean package
```

### 1) Running DFT

This example is taken from the example-suite of [TornadoVM](https://github.com/beehive-lab/TornadoVM):

How to run it?

```bash
tornado nyjavasig.DFT <inputSize> <version:sequential|tornadoVM> <numIterations>
```

Example:

```bash
# Running Sequential
tornado nyjavasig.DFT 65534 sequential 10

# Running Parallel Version with TornadoVM
tornado nyjavasig.DFT 65534 tornado 10
```

Changing device?
```bash
# Running Parallel Version with TornadoVM
tornado --debug -Ds0.t0.device=0:1 nyjavasig.DFT 65534 tornado 10
```

Print Kernel:
```bash
# Running Parallel Version with TornadoVM
tornado --printKernel --debug -Ds0.t0.device=0:1 nyjavasig.DFT 65534 tornado 10
```


### 2) Running on FPGAs

Note, you need an FPGA to run these demos.

##### Run DFT on the FPGA

```bash
tornado --debug --printKernel -Ds0.t0.device=0:1 -Dtornado.fpga.conf.file=dftFPGA.conf nyjavasig.DFT 8192 tornado 5
```

Running with Emulation mode:

```bash
export CL_CONTEXT_EMULATOR_DEVICE_INTELFPGA=1
tornado --debug --printKernel -Ds0.t0.device=0:1 -Dtornado.fpga.conf.file=dftFPGA.conf nyjavasig.DFT 8192 tornado 5
```

##### Running BlackScholes 

```bash
tornado --debug -Ds0.t0.device=0:1 -Dtornado.fpga.conf.file=blackScholes.conf nyjavasig.BlackScholes 33554432
```

### 3) Running Live Task Migration 

Demo using the Client-Server application to change the devices through the client:

How to run it?:

```bash
## Run Server in one terminal
./runServer.sh

## Client in another terminal
./runClient.sh
```

### 4) Python 


```bash
cd python 

## Run TornadoVM accelerated (512 x 512 Doubles)
./runPython.sh mxmWithTornadoVM.py

## Run CPU GraalVM 
./runPython.sh mxm.py
```


### 5) Running TornadoVM with Ideal Graph Visualizer (IGV)

Run IGV 20.2.0 

```bash
~/Downloads/20.2.0/idealgraphvisualizer/bin/idealgraphvisualizer &
```

Run an example (vector add)
```bash
tornado --igv nyjavasig.TestTornado
```
