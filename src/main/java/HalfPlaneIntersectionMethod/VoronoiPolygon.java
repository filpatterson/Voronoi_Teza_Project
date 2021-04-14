package HalfPlaneIntersectionMethod;

import java.awt.*;
import java.util.ArrayList;

public class VoronoiPolygon extends Polygon {
    //  edges of the polygon
    public ArrayList<Line> edges;

    //  vertices of the polygon
    public ArrayList<Point> vertices;

    /**
     * constructor that builds polygon out of given vertices
     * @param vertices vertices of the polygon
     */
    public VoronoiPolygon(ArrayList<Point> vertices) {
        //  get all vertices, prepare array for edges of polygon, find last index of the vertices
        this.vertices = vertices;
        this.edges = new ArrayList<>();
        int lastIndex = vertices.size() - 1;

        //  construct edges of the polygon
        for (int i = 0; i < lastIndex; i++)
            this.edges.add(new Line(vertices.get(i), vertices.get(i + 1)));

        this.edges.add(new Line(vertices.get(lastIndex), vertices.get(0)));
    }

    /**
     * check if line slices polygon (intersects it two times)
     * @param line line that intersects polygon
     * @return true if line slices polygon, false if not
     */
    public boolean checkSliceByLine(Line line) {
        //  how many times line intersects polygon
        short intersectionCounter = 0;

        //  iterate through each edge and check if it intersects with line
        for (Line edge : edges) {
            if (edge.intersectsLine(line)) {
                intersectionCounter++;
                if (intersectionCounter == 2)
                    return true;
            }
        }

        //  line either does not intersect polygon or intersects only once
        return false;
    }

    /**
     * find half polygon containing site by slicing polygon
     * @param perpendicular line that slices polygon
     * @param site point that must be inside polygon
     * @return half polygon with site inside
     */
    public VoronoiPolygon findHalfPolygon(Line perpendicular, Site site) {
        //  counter of found slices
        short foundSlices = 0;

        //  array list for vertices of half plane and new polygon reference
        ArrayList<Point> newVertices = new ArrayList<>();
        VoronoiPolygon foundHalfVoronoiPolygon;

        for (Line edge : edges) {
            //  if current edge intersects line
            if (edge.intersectsLine(perpendicular)) {
                //  find intersection point, add it to vertices list if no error detected and increment slice counter
                Point intersection = edge.findIntersection(perpendicular);
                if (intersection != null) {
                    newVertices.add(intersection);
                    foundSlices++;
                }

                if (foundSlices == 2) {
                    //  create half polygon based on found vertices and check if site is inside
                    foundHalfVoronoiPolygon = new VoronoiPolygon(newVertices);
                    foundHalfVoronoiPolygon.setDrawable();
                    if(foundHalfVoronoiPolygon.contains(site))
                        return foundHalfVoronoiPolygon;

                        //  else reset half polygon and start forming half polygon from second half
                    else {
                        foundSlices++;
                        newVertices.clear();
                        newVertices.add(intersection);
                    }
                }
            }

            //  add each vertice that is a part of half polygon
            if (foundSlices > 0)
                newVertices.add(edge.getSecondPoint());
        }

        //  come again through starting point to form second half polygon
        for(Line edge : edges) {
            if (edge.intersectsLine(perpendicular)) {
                Point intersection = edge.findIntersection(perpendicular);
                if (intersection != null) {
                    newVertices.add(intersection);
                    return new VoronoiPolygon(newVertices);
                }
            }
            newVertices.add(edge.getSecondPoint());
        }

        return this;
    }

    /**
     * form drawable part of polygon
     */
    public void setDrawable() {
        //  get amount of points
        npoints = vertices.size();

        //  define arrays of x and y coordinates of the polygon vertices
        xpoints = new int[npoints];
        ypoints = new int[npoints];
        for (int i = 0; i < npoints; i++) {
            xpoints[i] = (int) vertices.get(i).x;
            ypoints[i] = (int) vertices.get(i).y;
        }
    }
}
