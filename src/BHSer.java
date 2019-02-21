import java.awt.geom.Point2D;
import java.io.*;

public class BHSer {
    public static void main(String[] args) throws IOException {
        int N, numSteps;
        final double theta, length = Const.length, mass = Const.M;

        File file1 = new File("raw/time_bh_serial.txt");
        File file2 = new File("raw/data_bh_serial.ods");
        Lib.resetOutputFile("raw/data_bh_serial.ods");

        /* Check if arguments are present, otherwise prompts user for input */
        String[] data = Lib.setParamaters(args);

        N = Integer.valueOf(data[0]);
        numSteps = Integer.valueOf(data[1]);
        theta = Double.valueOf(data[2]);

        BHBody[] bodies = Lib.createBHBodies(N, mass);

        Point2D.Double origo = new Point2D.Double(0, 0);
        BHQuadrant quad;
        BHQuadTree tree;
        double timeBuild = 0, timeForce = 0, timeMove = 0;
        long t1 = System.nanoTime(), start;
        for (int i = 0; i < numSteps; i++) {

            if (i % 1500 == 0) Lib.logPositions(bodies, i, file2);

            start = System.nanoTime();
            quad = new BHQuadrant(origo, length);
            tree = new BHQuadTree(quad, theta);

            for (int j = 0; j < N; j++)
                if (bodies[j].inQuadrant(quad))
                    tree.insert(bodies[j]);
            timeBuild += System.nanoTime() - start;

            start = System.nanoTime();
            for (int j = 0; j < N; j++) {
                bodies[j].nullForce();
                tree.updateForcesOn(bodies[j]);
            }
            timeForce += System.nanoTime() - start;

            start = System.nanoTime();
            for (int j = 0; j < N; j++)
                bodies[j].updateState();
            timeMove += System.nanoTime() - start;

        }
        double time = Lib.time(t1, System.nanoTime())/1e9;
        String line = String.format("BHSer;%d;%d;%.2f;%s;time;%f", N, numSteps, theta, "n/a", time);
        Lib.logLineToFile(line, file1);
        try { Lib.logMeta(N, numSteps, Const.theta, 1, file2); } catch (IOException e) {}
        try { Lib.logSummary( timeBuild/1e9, timeForce/1e9, timeMove/1e9, file2); } catch (IOException e) {}
        System.out.println(line);
    }
}

