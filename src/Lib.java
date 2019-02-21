import java.awt.geom.Point2D;
import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class Lib {

    public static String[] setParamaters(String[] args){
        String[] params = new String[4];
        if (args.length != 4){
            Scanner scan = new Scanner(System.in);
            System.out.println("enter <numBodies> <numSteps> <far> <workers> :");
            String input = scan.nextLine();
            params = input.split(" ");

            if (params.length != 4) {
                System.err.print("usage: <numBodies> <numSteps> <far> <workers>");
                System.exit(1);
            }
        } else {
            params[0] = args[0];
            params[1] = args[1];
            params[2] = args[2];
            params[3] = args[3];
        }
        return params;
    }

    public static double time(long t1, long t2){
        return (double) (t2 - t1);
    }

    public static BHBody[] createBHBodies(int N, double mass){
        double px, py, vx, vy;
        Random rand = new Random();
        Point2D.Double zeroForce = new Point2D.Double(0, 0);
        BHBody[] bodies = new BHBody[N];

        for (int body = 0; body < N; body++) {
            px = Lib.spitCoord(rand);
            py = Lib.spitCoord(rand);
            vx = Lib.spitCoord(rand);
            vy = Lib.spitCoord(rand);

            Point2D.Double p = new Point2D.Double(px, py);
            Point2D.Double v = new Point2D.Double(vx, vy);
            bodies[body] = new BHBody(p, v, zeroForce, mass);
        }
        return bodies;
    }

    public static Point2D.Double createNSQBody(Random r){
        double x = spitCoord(r);
        double y = spitCoord(r);
        return new Point2D.Double(x, y);
    }

    public static double spitCoord(Random r){
        if (r.nextBoolean())
            return r.nextDouble();
        else
            return -r.nextDouble();
    }

    public static void resetOutputFile(String path) throws IOException {
        File file = new File(path);
        Writer write = new FileWriter(file);
        write.write("");
        write.close();
    }

    public static void logPositions(BHBody[] b, int cnt, File file) throws IOException{
        StringBuilder sb = new StringBuilder();
        sb.append(cnt);
        sb.append(";");
        for (int i = 0; i < 5; i++) {
            sb.append(entry(b[i].p, b[i].v));
        }
        sb.append("\n");

        Writer out = new BufferedWriter(new FileWriter(file, true));
        out.append(sb.toString());
        out.close();
    }

    public static void logPositions(Point2D.Double[] p, Point2D.Double[] v, int cnt, File file) throws IOException{
        StringBuilder sb = new StringBuilder();
        sb.append(cnt);
        sb.append(";");
        for (int i = 0; i < 5; i++) {
            sb.append(entry(p[i], v[i]));
        }
        sb.append("\n");

        Writer out = new BufferedWriter(new FileWriter(file, true));
        out.append(sb.toString());
        out.close();
    }

    private static String entry(Point2D.Double p, Point2D.Double v) {
        return String.format("%f;%f;%f;%f;", p.x, p.y, v.x, v.y);
    }

    public static String logSummary(int id, double calc, double b1, double move, double b2, File file) throws IOException {
        String summary = String.format("THREAD;%d;FORCE;%f;B1;%f;MOVE;%f;B2;%f", id, calc, b1, move, b2);
        logLineToFile(summary, file);
        return summary;
    }

    public static String logSummary(int id, double build, double b1, double calc, double b2, double move, double b3, File file) throws IOException {
        String summary = String.format("THREAD;%d;BUILD;%f;B1;%f;FORCE;%f;B2;%f;MOVE;%f;B3;%f", id, build, b1, calc, b2, move, b3);
        logLineToFile(summary, file);
        return summary;
    }

    public static String logSummary(double build, double calc, double move, File file) throws IOException {
        String summary = String.format("BUILD;%f;FORCE;%f;MOVE;%f", build, calc, move);
        logLineToFile(summary, file);
        return summary;
    }

    public static void logMeta(int bodies, int steps, double theta, int workers, File file) throws IOException {
        String s = String.format("GENERAL DATA;BODIES;%d;STEPS;%d;FAR;%f;WORKERS;%d", bodies, steps, theta, workers);
        logLineToFile(s, file);
    }

    public static void logLineToFile(String line, File file) throws IOException {
        Writer out = new BufferedWriter(new FileWriter(file, true));
        out.append(line);
        out.append("\n");
        out.close();
    }
}
