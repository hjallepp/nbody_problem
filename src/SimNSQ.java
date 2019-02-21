import java.io.IOException;

public class SimNSQ {
    public static void main(String[] args) throws IOException, InterruptedException{

        int bInit = 120, wInit = 1, off = 60, tests = 5;
        double tInit = Const.theta;
        String[] input;
        String bodies, steps = args[0], theta = Double.toString(tInit), workers;

        Lib.resetOutputFile("raw/time_nsq_serial.txt");
        Lib.resetOutputFile("raw/time_nsq_parallel.txt");

        Lib.resetOutputFile("raw/data_nsq_serial.ods");
        Lib.resetOutputFile("raw/data_nsq_parallel.ods");

        /**
         * Run NSQSer simulations
         */
        for (int b = 0; b < 3; b++) {
            bodies = Integer.toString(bInit + b*off);
            // steps
            // theta
            workers = Integer.toString(wInit);

            input = new String[]{bodies, steps, theta, workers};

            for (int t = 0; t < tests; t++) {
                NSQSer.main(input);
            }
        }

        /**
         * Run NSQPar simulations
         */
        for (int b = 0; b < 3; b++) {
            for (int w = 0; w < 4; w++) {
                bodies = Integer.toString(bInit + b*off);
                // steps
                // theta
                workers = Integer.toString(wInit + w);


                input = new String[]{bodies, steps, theta, workers};

                for (int t = 0; t < tests; t++) {
                    NSQPar.main(input);
                }
            }
        }
    }
}
