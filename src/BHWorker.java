import java.awt.geom.Point2D;
import java.io.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class BHWorker extends Thread {
    private int id, N, numSteps, numWorkers;
    private int stripSize;
    final private double length, theta;
    private BHBody[] body;
    private BHQuadTree tree;
    private CyclicBarrier barrier;
    private Bag bag;
    private File file;

    public BHWorker (int id, int n, int numSteps, double theta, int numWorkers, BHQuadTree tree, BHBody[] body, CyclicBarrier barrier, Bag bag, File file) {
        this.id = id;
        this.N = n;
        this.numSteps = numSteps;
        this.numWorkers = numWorkers;
        this.length = Const.length;
        this.theta = theta;
        this.stripSize = n / numSteps;
        this.body = body;
        this.tree = tree;
        this.barrier = barrier;
        this.bag = bag;
        this.file = file;
    }

    public void run()  {
        int task, first, last, step = 0;
        double timeB1 = 0, timeB2 = 0, timeB3 = 0, timeBuild = 0, timeForce = 0, timeMove = 0, t1, t2;
        Point2D.Double origo = new Point2D.Double(0, 0);
        BHQuadrant quad = new BHQuadrant(origo, length);

        while(step++ < numSteps) {
            /* Build a updated tree */
            if (id == 0){
                t1 =System.nanoTime();
                tree = new BHQuadTree(quad, theta);
                for (int i = 0; i < N; i++) {
                    tree.insert(body[i]);
                }
                bag.newBag();

                if ((step % 1500) == 0) try { Lib.logPositions(body, step, file); } catch (IOException e) {}
                t2 = System.nanoTime();
                timeBuild += t2 - t1;
            }

            timeB1 += await(barrier);

            t1 =System.nanoTime();
            while((task = bag.getTask()) < N) {
                body[task].nullForce();
                tree.updateForcesOn(body[task]);
            }
            t2 = System.nanoTime();
            timeForce += t2 - t1;

            timeB2 += await(barrier);

            t1 =System.nanoTime();

            first = id * stripSize;
            last = (id == numWorkers - 1) ? (N - 1) : (first + stripSize - 1);
            for(int i = first; i <= last; i++) {
                body[i].updateState();
            }
            t2 = System.nanoTime();
            timeMove += t2 - t1;

            timeB3 += await(barrier);
        }
        /**
         * Print summary of work distribution
         */
        try { Lib.logSummary(id, timeBuild/1e9, timeB1/1e9, timeForce/1e9, timeB2/1e9, timeMove/1e9, timeB3/1e9, file); } catch (IOException e) {}
    }

    private double await(CyclicBarrier barrier) {
        long start = System.nanoTime();
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();
        return (double) (end - start);
    }
}
