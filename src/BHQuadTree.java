public class BHQuadTree {

    private final double theta;

    private BHBody body;
    private BHQuadrant quad;
    private BHQuadTree NW;
    private BHQuadTree NE;
    private BHQuadTree SW;
    private BHQuadTree SE;

    public BHQuadTree(BHQuadrant q, double t) {
        this.theta = t;
        this.quad = q;
        this.body = null;
        this.NW = null;
        this.NE = null;
        this.SW = null;
        this.SE = null;
    }

    public void insert(BHBody b) {

        if (body == null) {
            body = b;
            return;
        }

        if ( !leaf() ) {
            body = body.combine(b);
            putBody(b);
        } else {
            NW = new BHQuadTree(quad.NorthWest(), this.theta);
            NE = new BHQuadTree(quad.NorthEast(), this.theta);
            SE = new BHQuadTree(quad.SouthEast(), this.theta);
            SW = new BHQuadTree(quad.SouthWest(), this.theta);

            putBody(body);
            putBody(b);
            body = body.combine(b);
        }
    }

    private void putBody(BHBody b) {
        if (b.inQuadrant(quad.NorthWest()))
            NW.insert(b);
        else if (b.inQuadrant(quad.NorthEast()))
            NE.insert(b);
        else if (b.inQuadrant(quad.SouthEast()))
            SE.insert(b);
        else if (b.inQuadrant(quad.SouthWest()))
            SW.insert(b);
    }

    private boolean leaf() {
        return (NW == null && NE == null && SW == null && SE == null);
    }

    public void updateForcesOn(BHBody b) {
        if (body == null || b.equals(body))
            return;

        if (leaf())
            b.determineForceOn(body);

        else {
            double s = quad.length();
            double d = body.distanceTo(b);

            if ((s / d) < theta)
                b.determineForceOn(body);

            else {
                NW.updateForcesOn(b);
                NE.updateForcesOn(b);
                SW.updateForcesOn(b);
                SE.updateForcesOn(b);
            }
        }
    }
}