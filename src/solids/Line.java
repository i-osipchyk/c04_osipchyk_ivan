package solids;

import transforms.Point3D;

import java.awt.*;
import java.util.Arrays;

public class Line extends Solid {
    public Line(Color color, Point3D... points3D) {
        this.vb.addAll(Arrays.asList(points3D));

        addIndices(0, 1);

        this.color = color;
    }
}