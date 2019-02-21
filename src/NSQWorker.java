import java.io.File;
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class NSQWorker extends Thread {
    private int id;
    private NSQPar uni;
    private CyclicBarrier barrier;
    private File file;

    public NSQWorker(int id, NSQPar uni, CyclicBarrier b, File f) {
        this.id = id;
        this.uni = uni;
        this.barrier = b;
        this.file = f;
    }

    public void run() {
        double timeB1 = 0, timeB2 = 0, timeForce = 0, timeMove = 0;
        int step = 0;

        while(step++ < uni.numSteps) {
//            uni.calculateForces(id);
            timeForce += force();
            timeB1 += await(barrier);

//            uni.moveBodies(id);
            timeMove += move();
            timeB2 += await(barrier);

            if ((step % 500) == 0 && (id == 0)) try { Lib.logPositions(uni.p, uni.v, step, file); } catch (IOException e){}
        }
        /**
         * Print summary of work distribution
         */
        try { Lib.logSummary(id, timeForce/1e9, timeB1/1e9, timeMove/1e9, timeB2/1e9, file); } catch (IOException e) {}

    }

    private double force(){
        long t1, t2;
        t1 = System.nanoTime();
        uni.calculateForces(id);
        t2 = System.nanoTime();
        return (double)t2-t1;
    }

    private double move(){
        long t1, t2;
        t1 = System.nanoTime();
        uni.moveBodies(id);
        t2 = System.nanoTime();
        return (double)t2-t1;
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