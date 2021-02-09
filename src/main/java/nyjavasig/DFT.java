package nyjavasig;

import uk.ac.manchester.tornado.api.TaskSchedule;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.collections.math.TornadoMath;

import java.util.Random;

/**
 * Direct Fourier Transform example used for demonstrations purposes.
 * {@url https://en.wikipedia.org/wiki/Discrete_Fourier_transform}
 *
 * This example is taken from the example-suite of the TornadoVM distribution:
 * {@url https://github.com/E2Data/TornadoVM/blob/master/examples/src/main/java/uk/ac/manchester/tornado/examples/dynamic/DFTDynamic.java#L26}
 *
 * How to run it?
 *
 * <code>
 * $ tornado nyjavasig.DFT <inputSize> <version:sequential|tornadoVM> <numIterations>
 * </code>
 *
 * How to run on the FPGA?
 *
 * Assuming the FPGA is the device 0:1
 *
 * <code>
 * $ tornado --debug --printKernel -Ds0.t0.device=0:1 -Dtornado.fpga.conf.file=dftFPGA.conf nyjavasig.DFT 8192 tornado 5
 * </code>
 *
 * Running on the FPGA with emulation mode:
 *
 * <code>
 * $ export CL_CONTEXT_EMULATOR_DEVICE_INTELFPGA=1
 * $ tornado --debug --printKernel -Ds0.t0.device=0:1 -Dtornado.fpga.conf.file=dftFPGA.conf nyjavasig.DFT 8192 tornado 5
 * </code>
 *
 */
public class DFT {

    private static boolean CHECK_RESULT = false;

    private static void computeDft(float[] inreal, float[] inimag, float[] outreal, float[] outimag) {
        int n = inreal.length;
        for (@Parallel int k = 0; k < n; k++) { // For each output element
            float sumreal = 0;
            float sumimag = 0;
            for (int t = 0; t < n; t++) { // For each input element
                float angle = ((2 * TornadoMath.floatPI() * t * k) / (float) n);
                sumreal += (inreal[t] * (TornadoMath.floatCos(angle)) + inimag[t] * (TornadoMath.floatSin(angle)));
                sumimag += -(inreal[t] * (TornadoMath.floatSin(angle)) + inimag[t] * (TornadoMath.floatCos(angle)));
            }
            outreal[k] = sumreal;
            outimag[k] = sumimag;
        }
    }

    private static boolean validate(int size, float[] inReal, float[] inImag, float[] outReal, float[] outImag, int[] inputSize) {
        boolean val = true;
        float[] outRealTor = new float[size];
        float[] outImagTor = new float[size];

        computeDft(inReal, inImag, outRealTor, outImagTor);

        for (int i = 0; i < size; i++) {
            if (Math.abs(outImagTor[i] - outImag[i]) > 0.5) {
                System.out.println(outImagTor[i] + " vs " + outImag[i] + "\n");
                val = false;
                break;
            }
            if (Math.abs(outReal[i] - outRealTor[i]) > 0.5) {
                System.out.println(outReal[i] + " vs " + outRealTor[i] + "\n");
                val = false;
                break;
            }
        }
        System.out.println("Is valid?: " + val + "\n");
        return val;
    }

    public static void main(String[] args) {

        if (args.length < 3) {
            System.out.println("Usage: <size> <mode:tornado|sequential> <iterations>");
            System.exit(-1);
        }

        final int size = Integer.parseInt(args[0]);
        String executionType = args[1];
        int iterations = Integer.parseInt(args[2]);

        long end,start;

        TaskSchedule graph = null;
        float[] inReal;
        float[] inImag;
        float[] outReal;
        float[] outImag;
        int[] inputSize;

        inReal = new float[size];
        inImag = new float[size];
        outReal = new float[size];
        outImag = new float[size];
        inputSize = new int[1];

        inputSize[0] = size;

        Random r = new Random();
        for (int i = 0; i < size; i++) {
            inReal[i] = r.nextFloat();
            inImag[i] = r.nextFloat();
        }

        if (!"sequential".equals(executionType)) {
            graph = new TaskSchedule("s0") //
                    .task("t0", DFT::computeDft, inReal, inImag, outReal, outImag) //
                    .streamOut(outReal, outImag);
        }

        for (int i = 0; i < iterations; i++) {
            if ("sequential".equals(executionType)) {
                start = System.nanoTime();
                computeDft(inReal, inImag, outReal, outImag);
                end = System.nanoTime();
            } else {
                start = System.nanoTime();
                graph.execute();
                end = System.nanoTime();
            }
            double seconds = (end - start) * 1e-9;
            System.out.println("Total time:  " + (end - start) + " ns  -- " + seconds + " (s)");
        }

        if (CHECK_RESULT) {
            if (validate(size, inReal, inImag, outReal, outImag, inputSize)) {
                System.out.println("Validation: " + "SUCCESS " + "\n");
            } else {
                System.out.println("Validation: " + " FAIL " + "\n");
            }
        }
    }
}
