package nyjavasig;

import java.util.Random;

import uk.ac.manchester.tornado.api.TaskSchedule;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.collections.math.TornadoMath;

/**
 * BlackScholes implementation adapted from AMD-OpenCL, and TornadoVM examples suite.
 *
 * How to run:
 *
 * <code>
 * $ tornado nyjavasig.BlackScholes
 * </code>
 *
 * How to run on an FPGA?
 * For example, FPGA is device 0:1
 *
 * <code>
 * $ tornado --debug -Ds0.t0.device=0:1 -Dtornado.fpga.conf.file=blackScholes.conf nyjavasig.BlackScholes 33554432
 * </code>
 *
 * Note that we pass a configuration file for the FPGA: The configuration file contains the following:
 *
 * <code>
 * [device]
 * DEVICE_NAME=p385a_sch_ax115
 *
 * [options]
 * FLAGS=-v -fast-compile -high-effort -fp-relaxed -report -incremental -profile
 * DIRECTORY_BITSTREAM=fpga-source-comp-blackscholes/
 * </code>
 *
 */
public class BlackScholes {

    private static final int WARM_UP_ITERATIONS = 100;

    private static void blackScholesKernel(float[] input, float[] callResult, float[] putResult) {
        for (@Parallel int idx = 0; idx < callResult.length; idx++) {
            float rand = input[idx];
            final float S_LOWER_LIMIT = 10.0f;
            final float S_UPPER_LIMIT = 100.0f;
            final float K_LOWER_LIMIT = 10.0f;
            final float K_UPPER_LIMIT = 100.0f;
            final float T_LOWER_LIMIT = 1.0f;
            final float T_UPPER_LIMIT = 10.0f;
            final float R_LOWER_LIMIT = 0.01f;
            final float R_UPPER_LIMIT = 0.05f;
            final float SIGMA_LOWER_LIMIT = 0.01f;
            final float SIGMA_UPPER_LIMIT = 0.10f;
            final float S = S_LOWER_LIMIT * rand + S_UPPER_LIMIT * (1.0f - rand);
            final float K = K_LOWER_LIMIT * rand + K_UPPER_LIMIT * (1.0f - rand);
            final float T = T_LOWER_LIMIT * rand + T_UPPER_LIMIT * (1.0f - rand);
            final float r = R_LOWER_LIMIT * rand + R_UPPER_LIMIT * (1.0f - rand);
            final float v = SIGMA_LOWER_LIMIT * rand + SIGMA_UPPER_LIMIT * (1.0f - rand);

            float d1 = (TornadoMath.log(S / K) + ((r + (v * v / 2)) * T)) / v * TornadoMath.sqrt(T);
            float d2 = d1 - (v * TornadoMath.sqrt(T));
            callResult[idx] = S * cnd(d1) - K * TornadoMath.exp(T * (-1) * r) * cnd(d2);
            putResult[idx] = K * TornadoMath.exp(T * -r) * cnd(-d2) - S * cnd(-d1);
        }
    }

    private static float cnd(float X) {
        final float c1 = 0.319381530f;
        final float c2 = -0.356563782f;
        final float c3 = 1.781477937f;
        final float c4 = -1.821255978f;
        final float c5 = 1.330274429f;
        final float zero = 0.0f;
        final float one = 1.0f;
        final float two = 2.0f;
        final float temp4 = 0.2316419f;
        final float oneBySqrt2pi = 0.398942280f;
        float absX = TornadoMath.abs(X);
        float t = one / (one + temp4 * absX);
        float y = one - oneBySqrt2pi * TornadoMath.exp(-X * X / two) * t * (c1 + t * (c2 + t * (c3 + t * (c4 + t * c5))));
        return (X < zero) ? (one - y) : y;
    }

    private static boolean checkResult(float[] call, float[] put, float[] callPrice, float[] putPrice) {
        double delta = 1.8;
        for (int i = 0; i < call.length; i++) {
            if (Math.abs(call[i] - callPrice[i]) > delta) {
                System.out.println("call: " + call[i] + " vs gpu " + callPrice[i]);
                return false;
            }
            if (Math.abs(put[i] - putPrice[i]) > delta) {
                System.out.println("put: " + put[i] + " vs gpu " + putPrice[i]);
                return false;
            }
        }
        return true;
    }

    public static void blackScholes(int size) {
        Random random = new Random();
        float[] input = new float[size];
        float[] callPrice = new float[size];
        float[] putPrice = new float[size];
        float[] seqCall = new float[size];
        float[] seqPut = new float[size];
        for (int i = 0; i < size; i++) {
            input[i] = random.nextFloat();
        }

        TaskSchedule graph = new TaskSchedule("s0") //
                .streamIn(input) //
                .task("t0", BlackScholes::blackScholesKernel, input, callPrice, putPrice) //
                .streamOut(callPrice, putPrice);

        for (int i = 0; i < WARM_UP_ITERATIONS; i++) {
            graph.execute();
        }
        long start = System.nanoTime();
        graph.execute();
        long end = System.nanoTime();

        for (int i = 0; i < WARM_UP_ITERATIONS; i++) {
            blackScholesKernel(input, seqCall, seqPut);
        }
        long start2 = System.nanoTime();
        blackScholesKernel(input, seqCall, seqPut);
        long end2 = System.nanoTime();

        boolean results = checkResult(seqCall, seqPut, callPrice, putPrice);

        System.out.println("Validation " + results + " \n");
        System.out.println("Seq     : " + (end2 - start2) + " (ns)");
        System.out.println("Parallel: " + (end - start) + " (ns)");
        System.out.println("Speedup : " + ((end2 - start2) / (end - start)) + "x");
    }

    public static void main(String[] args) {
        System.out.println("BlackScholes TornadoVM");
        int size = 1024;
        if (args.length > 0) {
            try {
                size = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }
        System.out.println("Input size: " + size + " \n");
        blackScholes(size);
    }
}
