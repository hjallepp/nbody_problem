import java.awt.geom.Point2D;
import java.io.*;
import java.util.Random;

public class NSQSer {
    private int N;
    private Point2D.Double[] p, v, f; //position, velocity, force
    private double[] m; //and mass for each body

    public NSQSer(int n) {
        N = n;
        p = new Point2D.Double[N];
        v = new Point2D.Double[N];
        f = new Point2D.Double[N];
        m = new double[N];

        Point2D.Double force = new Point2D.Double(0.0, 0.0);
        Random r = new Random();
        for(int i = 0; i < N; i++) {
            p[i] = Lib.createNSQBody(r);
            v[i] = Lib.createNSQBody(r);
            f[i] = force;
            m[i] = Const.M;
        }
    }

    public void calculateForces() {
        double distance, F;
        Point2D.Double direction;
        double G = Const.G;

        for(int i = 0; i < N-1; i++){
            for(int j = i + 1; j < N; j++) {
                distance = Math.sqrt(Math.pow((p[i].x - p[j].x), 2) + Math.pow((p[i].y - p[j].y), 2));
                F = (G*m[i]*m[j]) / Math.pow(distance,2);
                direction = new Point2D.Double(p[j].x-p[i].x, p[j].y-p[i].y);

                f[i].x = f[i].x + F*direction.x/distance;
                f[j].x = f[j].x - F*direction.x/distance;
                f[i].y = f[i].y + F*direction.y/distance;
                f[j].y = f[j].y - F*direction.y/distance;
            }
        }
    }

    public void moveBodies() {
        Point2D.Double deltav; //dv = f/m * DT
        Point2D.Double deltap; //dp = (v + dv/2) * DT
        double DT = Const.DT;
        for(int i = 0; i < N; i++) {
            deltav = new Point2D.Double((f[i].x/m[i])*DT, (f[i].y/m[i])*DT);
            deltap = new Point2D.Double((v[i].x + deltav.x/2) * DT, (v[i].y + deltav.y/2) * DT);
            v[i].x = v[i].x + deltav.x;
            v[i].y = v[i].y + deltav.y;
            p[i].x = p[i].x + deltap.x;
            p[i].y = p[i].y + deltap.y;
            f[i].x = f[i].y = 0.0; //reset force vector
        }

    }

    public static void main(String[] args) throws IOException {
        int N, numSteps;

        String[] data = Lib.setParamaters(args);

        N = Integer.valueOf(data[0]);
        numSteps = Integer.valueOf(data[1]);

        NSQSer uni = new NSQSer(N);

        File file1 = new File("raw/time_nsq_serial.txt");
        File file2 = new File("raw/data_nsq_serial.ods");

        long t1 = System.nanoTime();
        for(int i = 0; i < numSteps; i++) {
            uni.calculateForces();
            uni.moveBodies();

            if(i % 100 == 0) Lib.logPositions(uni.p, uni.v, i, file2);
        }
        double time = Lib.time(t1, System.nanoTime())/1e9;
        String line = String.format("NSQSer;%d;%d;%s;%s;time;%f", N, numSteps, "n/a", "n/a", time);
        Lib.logLineToFile(line, file1);
        try { Lib.logMeta(N, numSteps, Const.theta, 1, file2); } catch (IOException e) {}
        System.out.println(line);
    }
}
