package solids;


import transforms.Point3D;

public class Cube extends Solid {

    public Cube() {

        vb.add(new Point3D(0,0,0));
        vb.add(new Point3D(0.5,0,0));
        vb.add(new Point3D(0.5,0.5,0));
        vb.add(new Point3D(0,0.5,0));
        vb.add(new Point3D(0,0,0.5));
        vb.add(new Point3D(0.5,0,0.5));
        vb.add(new Point3D(0.5,0.5,0.5));
        vb.add(new Point3D(0,0.5,0.5));

        addIndices(
                0, 1,
                1, 2,
                2, 3,
                3, 0,
                0, 4,
                1, 5,
                2, 6,
                3, 7,
                4, 5,
                5, 6,
                6, 7,
                7, 4
        );
    }
}

