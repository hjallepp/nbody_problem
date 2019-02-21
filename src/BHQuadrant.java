import java.awt.geom.Point2D;

public class BHQuadrant {
    private Point2D.Double middle;
    private double length;

    public BHQuadrant(Point2D.Double middle, double length) {
        this.middle = middle;
        this.length = length;
    }

    public double length() {
        return length;
    }

    //method to check if point p is in this quadrant
    public boolean contains(Point2D.Double p) {
        if(p.x <= middle.x + length/2.0 &&
                p.x >= middle.x - length/2.0 &&
                p.y <= middle.y + length/2.0 &&
                p.y >= middle.y - length/2.0) return true;
        else return false;
    }

    public BHQuadrant NorthWest() {
        double x = this.middle.x - this.length/4.0;
        double y = this.middle.y + this.length/4.0;
        Point2D.Double p = new Point2D.Double(x, y);
        double len = this.length/2.0;
        BHQuadrant northWest = new BHQuadrant(p, len);
        return northWest;
    }

    public BHQuadrant NorthEast() {
        double x = this.middle.x + this.length/4.0;
        double y = this.middle.y + this.length/4.0;
        Point2D.Double p = new Point2D.Double(x,y);
        double len = this.length/2.0;
        BHQuadrant northEast = new BHQuadrant(p, len);
        return northEast;
    }

    public BHQuadrant SouthWest() {
        double x = this.middle.x - this.length/4.0;
        double y = this.middle.y - this.length/4.0;
        Point2D.Double p = new Point2D.Double(x, y);
        double len = this.length/2.0;
        BHQuadrant southWest = new BHQuadrant(p, len);
        return southWest;

    }

    public BHQuadrant SouthEast() {
        double x = this.middle.x + this.length/4.0;
        double y = this.middle.y - this.length/4.0;
        Point2D.Double p = new Point2D.Double(x, y);
        double len = this.length/2.0;
        BHQuadrant southEast = new BHQuadrant(p, len);
        return southEast;
    }


}
