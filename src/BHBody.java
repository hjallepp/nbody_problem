import java.awt.geom.Point2D;

public class BHBody {
    private double mass;
    public Point2D.Double p, v, f;

    public BHBody(Point2D.Double p, Point2D.Double v, Point2D.Double f, double mass) {
        this.p = p;
        this.v = v;
        this.f = f;
        this.mass = mass;
    }

    public void updateState(){
        Point2D.Double deltav, deltap;
        double DT = Const.DT;

        deltav = new Point2D.Double((f.x/mass) * DT, (f.y/mass) * DT);
        deltap = new Point2D.Double((v.x + deltav.x/2) * DT,(v.y + deltav.y/2) * DT);

        v.x = v.x + deltav.x;
        v.y = v.y + deltav.y;
        p.x = p.x + deltap.x;
        p.y = p.y + deltap.y;
    }

    //distance between this BHBody and body2
    public double distanceTo(BHBody b2) {
        double deltapx = p.x - b2.p.x;
        double deltapy = p.y - b2.p.y;
        return Math.sqrt(deltapx*deltapx + deltapy*deltapy);
    }

    public void nullForce() {
        f.x = 0.0;
        f.y = 0.0;
    }

    //calculates the net force between this BHBody and body2,
    //and add the calculated force to the net force on body2
    public void determineForceOn(BHBody b) {
        double distance, magnitude;
        Point2D.Double direction;
        double G = Const.G;

        BHBody me = this; //the invoking body
        distance = me.distanceTo(b);
        magnitude = (G*me.mass*b.mass) / distance*distance;
        direction = new Point2D.Double(b.p.x - me.p.x, b.p.y - me.p.y);

        me.f.x = me.f.x + magnitude*direction.x/distance;
        me.f.y = me.f.y + magnitude*direction.y/distance;
    }

    public boolean inQuadrant(BHQuadrant q) {
        return q.contains(this.p);
    }

    public BHBody combine(BHBody b) {
        BHBody me = this;
        double totalMass = me.mass + b.mass;
        double x = (me.p.x * me.mass + b.p.x * b.mass) / totalMass;
        double y = (me.p.y * me.mass + b.p.y * b.mass) / totalMass;
        Point2D.Double p = new Point2D.Double(x, y);
        return new BHBody(p, me.v, b.v, totalMass);
    }

}