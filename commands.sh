# ############################################################################
# First Demo: DFT 
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
# Second Demo: Python 
# ############################################################################
cd python 

## Run TornadoVM accelerated (512 x 512 Doubles)
./runPython.sh mxmWithTornadoVM.py

## Run CPU GraalVM 
./runPython.sh mxm.py



# ############################################################################
# Third Demo: IGV  
# ############################################################################

Run IGV 20.2.0 

```bash
~/Downloads/20.2.0/idealgraphvisualizer/bin/idealgraphvisualizer &
```

Run an example (vector add)
```bash
tornado --igv nyjavasig.TestTornado
```


