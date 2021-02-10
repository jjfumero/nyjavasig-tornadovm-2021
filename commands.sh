# ############################################################################
# Demo: DFT 
# ############################################################################

## Run Sequential 
## FORMAT: program  <size> <type> <iterations>
tornado nyjavasig.DFT 8192 sequential 10

# Running Parallel Version with TornadoVM
tornado nyjavasig.DFT 8192 tornado 10

## Run with debug Device Information
tornado --debug nyjavasig.DFT 8192 tornado 10

## print kernel
tornado --debug --printKernel nyjavasig.DFT 8192 tornado 10

## Change Device:
tornado --debug --printKernel -Ds0.t0.device=0:2 nyjavasig.DFT 8192 tornado 10

## DFT on the FPGA
tornado --debug -Ds0.t0.device=0:1 nyjavasig.DFT 8192 tornado 10

# Running Parallel Version with TornadoVM
tornado --printKernel --debug -Ds0.t0.device=0:1 nyjavasig.DFT 8192 tornado 10


# ############################################################################
# FPGA Demo: Assuming FPGA is device 2
# ############################################################################

## Running sequential
tornado --debug --printKernel -Ds0.t0.device=0:2 -Dtornado.fpga.conf.file=dftFPGAEmulation.conf nyjavasig.DFT 8192 sequential 5

## Running emulation mode
tornado --debug --printKernel -Ds0.t0.device=0:2 -Dtornado.fpga.conf.file=dftFPGAEmulation.conf nyjavasig.DFT 8192 tornado 5

## Running on real hardware
tornado --debug --printKernel -Ds0.t0.device=0:2 -Dtornado.fpga.conf.file=dftFPGA.conf nyjavasig.DFT 8192 tornado 5

## Running Blaskscholes
tornado --debug --printKernel -Ds0.t0.device=0:2 -Dtornado.fpga.conf.file=blackScholes.conf nyjavasig.BlackScholes 33554432 5


# ############################################################################
# Third Demo: IGV  
# ############################################################################

#Run IGV 20.2.0 
~/Downloads/20.2.0/idealgraphvisualizer/bin/idealgraphvisualizer &


# Run an example (vector add)

tornado --igv nyjavasig.TestTornado


# ############################################################################
# Live Task Migration Demo: 
# ############################################################################

runServer.sh

## In another terminal

runClient.sh
