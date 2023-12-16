package renderer;

import rasterize.LineRasterizer;
import solids.Solid;
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;
import transforms.Vec3D;

import java.awt.*;
import java.util.ArrayList;

public class WiredRenderer {
    private LineRasterizer lineRasterizer;
    private Mat4 view, proj;

    public WiredRenderer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
        this.view = new Mat4Identity();
        this.proj = new Mat4Identity();
    }

    public void render(Solid solid) {

        for (int i = 0; i < solid.getIb().size(); i += 2) {
            int indexA = solid.getIb().get(i);
            int indexB = solid.getIb().get(i + 1);

            Point3D a = solid.getVb().get(indexA);
            Point3D b = solid.getVb().get(indexB);

            a = a.mul(solid.getModel()).mul(view).mul(proj);
            b = b.mul(solid.getModel()).mul(view).mul(proj);

            double w1 = a.getW();
            double w2 = b.getW();

            if (!((-w1 <= a.getX() && a.getX() <= w1) && (-w1 <= a.getY() && a.getY() <= w1) && (0 <= a.getZ() && a.getZ() <= w1)) ||
                    !((-w2 <= b.getX() && b.getX() <= w2) && (-w2 <= b.getY() && b.getY() <= w2) && (0 <= b.getZ() && b.getZ() <= w2))) {
                continue;
            }


            Vec3D v1;
            Vec3D v2;

            if (a.dehomog().isPresent() && b.dehomog().isPresent()) {
                v1 = a.dehomog().get();
                v2 = b.dehomog().get();
            }
            else
                continue;

            v1 = transformToWindow(v1);
            v2 = transformToWindow(v2);

            lineRasterizer.rasterize(
                    (int)Math.round(v1.getX()), (int)Math.round(v1.getY()),
                    (int)Math.round(v2.getX()), (int)Math.round(v2.getY()),
                    solid.getColor());
        }
    }

    public Vec3D transformToWindow(Vec3D p) {
        return p.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D((800 - 1) / 2., (600 - 1) / 2., 1));
    }

    public void renderScene(Solid... solids) {
        for (Solid solid : solids)
            render(solid);
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProj(Mat4 proj) {
        this.proj = proj;
    }
}
