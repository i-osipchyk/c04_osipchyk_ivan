package solids;

import transforms.Point3D;

public class Pyramid extends Solid{

    public Pyramid() {
        vb.add(new Point3D(0.5, 0.5, 0));
        vb.add(new Point3D(1, 0.5, 0));
        vb.add(new Point3D(1, 1, 0));
        vb.add(new Point3D(0.5, 1, 0));
        vb.add(new Point3D(0.75, 0.75, 1));

        addIndices(
                0, 1,
                1, 2,
                2, 3,
                3, 0,
                0, 4,
                1, 4,
                2, 4,
                3, 4
        );
    }
}
