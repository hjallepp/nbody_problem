import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;

public class NSQPar extends Thread {
    public int N, numSteps, numWorkers;


    public Point2D.Double[] p, v; //position, velocity
    public Point2D.Double[][] f; //force matrix
    public double[] m; //and mass for each body


    public NSQPar(int n, int s, int w) {

        this.N = n;
        this.numSteps = s;
        this.numWorkers = w;

        /* Create all bodies and give them initial stats */
        p = new Point2D.Double[N];
        v = new Point2D.Double[N];
        f = new Point2D.Double[numWorkers][N];
        m = new double[N];
        Random r = new Random();

        for(int i = 0; i < N; i++) {
            p[i] = Lib.createNSQBody(r);
            v[i] = Lib.createNSQBody(r);
            m[i] = Const.M;
        }
        for(int i = 0; i < numWorkers; i++) {
            for(int j = 0; j < N; j++) {
                f[i][j] = new Point2D.Double(0.0, 0.0); //Force begins at 0
            }
        }
    }

    public void calculateForces(int id) {
        double distance, magnitude;
        Point2D.Double direction;
        double G = Const.G;

        for(int i = id; i < N-1; i += numWorkers) {
            for(int j = i + 1; j < N; j++){
                distance = Math.sqrt(Math.pow((p[i].x - p[j].x), 2) + Math.pow((p[i].y - p[j].y), 2));
                magnitude = (G*m[i]*m[j]) / Math.pow(distance,2);
                direction = new Point2D.Double(p[j].x-p[i].x, p[j].y-p[i].y);
                f[id][i].x = f[id][i].x + magnitude*direction.x/distance;
                f[id][j].x = f[id][j].x - magnitude*direction.x/distance;
                f[id][i].y = f[id][i].y + magnitude*direction.y/distance;
                f[id][j].y = f[id][j].y - magnitude*direction.y/distance;
            }
        }
    }

    public void moveBodies(int w) {
        Point2D.Double deltav;
        Point2D.Double deltap;
        Point2D.Double force = new Point2D.Double(0.0, 0.0);
        double DT = Const.DT;

        for(int i = w; i < N; i += numWorkers) {
            //sum the forces on body i and reset f[*, i]
            for(int k = 0; k < numWorkers; k++) {
                force.x += f[k][i].x;
                f[k][i].x = 0.0;
                force.y += f[k][i].y;
                f[k][i].y = 0.0;
            }
            deltav = new Point2D.Double(force.x/m[i] * DT, force.y/m[i] * DT);
            deltap = new Point2D.Double((v[i].x + deltav.x/2) * DT,
                    (v[i].y + deltav.y/2) * DT);
            v[i].x = v[i].x + deltav.x;
            v[i].y = v[i].y + deltav.y;
            p[i].x = p[i].x + deltap.x;
            p[i].y = p[i].y + deltap.y;
            force.x = force.y = 0.0;

        }
    }

    public static void main(String[] args) throws IOException, InterruptedException{
        int N, numSteps, numWorkers;

        String[] data = Lib.setParamaters(args);
        N = Integer.valueOf(data[0]);
        numSteps = Integer.valueOf(data[1]);
        numWorkers = Integer.valueOf(data[3]);

        File file1 = new File("raw/time_nsq_parallel.txt");
        File file2 = new File("raw/data_nsq_parallel.ods");

        NSQPar uni = new NSQPar(N, numSteps, numWorkers);
        NSQWorker[] threads = new NSQWorker[numWorkers];
        CyclicBarrier barrier = new CyclicBarrier(uni.numWorkers);

        long t1 = System.nanoTime();
        for(int i = 0; i < numWorkers; i++) {
            threads[i] = new NSQWorker(i, uni, barrier, file2);
            threads[i].start();
        }

        for(int i = 0; i < uni.numWorkers; i++) {
            threads[i].join();
        }
        double time = Lib.time(t1, System.nanoTime())/1e9;
        String line = String.format("NSQPar;%d;%d;%s;%d;time;%f", N, numSteps, "n/a", numWorkers, time);
        Lib.logLineToFile(line, file1);
        try { Lib.logMeta(uni.N, uni.numSteps, Const.theta, uni.numWorkers, file2); } catch (IOException e) {}
        System.out.println(line);
    }
}
