package nyjavasig;

import uk.ac.manchester.tornado.api.TaskSchedule;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.annotations.Reduce;

/**
 * Hello world in TornadoVM. Example to show how to run with TornadoVM.
 *
 * To run it, install/configure TornadoVM.
 * {@url https://github.com/beehive-lab/tornadovm-installer/}
 *
 * Run on the default device:
 * $ tornado nyjavasig.TestTornado
 *
 * Run with device-debug info:
 * $ tornado --debug nyjavasig.TestTornado
 *
 * Print OpenCL kernel generated
 * $ tornado --debug --printKernel nyjavasig.TestTornado
 *
 */
public class TestTornado {

    public static void vectorMult(float[] a, float[] b, float[] c) {
        for (@Parallel int i = 0; i < a.length; i++) {
            c[i] = a[i] * b[i] + 12.0f;
        }
    }

    public static void main( String[] args ) {
        System.out.println( "Test TornadoVM - Running Vector Multiplication" );

        float[] a = new float[8192];
        float[] b = new float[8192];
        float[] c = new float[8192];

        new TaskSchedule("s0")
                .streamIn(a, b)
                .task("t0", TestTornado::vectorMult, a, b, c)
                .streamOut(c)
                .execute();
    }
}
