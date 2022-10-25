import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

public class Arc {

    private Point start;
    private Point end;
    private Node nodeStart;
    private Node nodeEnd;
    private boolean doDrawing;

    public Arc(Point start, Point end, Node n1, Node n2) {
        this.start = start;
        this.end = end;
        this.nodeStart = n1;
        this.nodeEnd = n2;
    }
    public boolean getDoDrawing(boolean doDrawing){
        return doDrawing;
    }
    public void setDoDrawing(boolean doDrawing) {
        this.doDrawing = doDrawing;
    }
    public Node getNodeStart() {
        return this.nodeStart;
    }
    public Node getNodeEnd() {
        return this.nodeEnd;
    }

    public void editArc(Point start, Point end) {
        this.start = start;
        this.end = end;
    }
    public void drawArc(Graphics g) {

        if (start != null && doDrawing) {
            g.setColor(Color.RED);
            g.drawLine(start.x, start.y, end.x, end.y);
            if (MyPanel.isGrafOrientat())
                drawArrow(g, start.x, start.y, end.x, end.y, MyPanel.node_diam, MyPanel.node_diam);
        }


    }
    public void drawArrow(Graphics g, int x0, int y0, int x1,
                          int y1, int headLength, int headAngle) {

        double offs = headAngle * Math.PI / 180.0;
        double angle = Math.atan2(y0 - y1, x0 - x1);
        int[] xs = {x1 + (int) (headLength * Math.cos(angle + offs)), x1,
                x1 + (int) (headLength * Math.cos(angle - offs))};
        int[] ys = {y1 + (int) (headLength * Math.sin(angle + offs)), y1,
                y1 + (int) (headLength * Math.sin(angle - offs))};
        g.drawLine(x0, y0, x1, y1);
        g.drawPolyline(xs, ys, 3);
        g.setColor(Color.GREEN);
        g.fillPolygon(xs, ys, 3);
    }
}
