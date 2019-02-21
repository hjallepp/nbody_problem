import java.awt.geom.Point2D;
import java.io.*;
import java.util.concurrent.CyclicBarrier;

public class BHPar {

    public static void main(String[] args) throws IOException, InterruptedException {
        int N, numSteps, numWorkers;
        final double theta, length = Const.length, mass = Const.M;
        String[] data;

        File file1 = new File("raw/time_bh_parallel.txt");
        File file2 = new File("raw/data_bh_parallel.ods");

        /* Check if arguments are present, otherwise prompts user for input */
        data  = Lib.setParamaters(args);

        N = Integer.valueOf(data[0]);
        numSteps = Integer.valueOf(data[1]);
        theta = Double.valueOf(data[2]);
        numWorkers = Integer.valueOf(data[3]);


        BHBody[] bodies = Lib.createBHBodies(N, mass);

        // Create parameters for the workers
        CyclicBarrier barrier = new CyclicBarrier(numWorkers);
        Bag bag = new Bag();
        Point2D.Double o = new Point2D.Double(0, 0);
        BHQuadrant quad = new BHQuadrant(o, length);
        BHQuadTree tree = new BHQuadTree(quad, theta);

        BHWorker[] threads = new BHWorker[N];
        long t1 = System.nanoTime();
        for (int i = 0; i < numWorkers; i++) {
            threads[i] = new BHWorker(i,N, numSteps, theta, numWorkers, tree, bodies, barrier,bag, file2);
            threads[i].start();
        }
        for (int i = 0; i < numWorkers; i++) {
            threads[i].join();
        }

        double time = Lib.time(t1, System.nanoTime())/1e9;
        String line = String.format("BHPar;%d;%d;%.2f;%d;time;%f", N, numSteps, theta, numWorkers, time);
        Lib.logLineToFile(line, file1);
        try { Lib.logMeta(N, numSteps, theta, numWorkers, file2); } catch (IOException e) {}
        System.out.println(line);
    }
}

