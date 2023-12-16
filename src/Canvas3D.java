import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import rasterize.RasterBufferedImage;
import renderer.WiredRenderer;
import solids.Cube;
import solids.Line;
import solids.Pyramid;
import solids.Solid;
import transforms.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Canvas3D {
    private JPanel panel;
    private RasterBufferedImage raster;
    private LineRasterizer lineRasterizer;
    private WiredRenderer wiredRenderer;
    private Solid pyramid, cube, lineX, lineY, lineZ;
    private Camera camera;
    private Mat4 projection;
    private double transl = 0.1;
    private boolean cubeMode = true;
    private boolean perspectiveProjection = true;

    public Canvas3D(int width, int height) {
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height);
        lineRasterizer = new LineRasterizerGraphics(raster);
        wiredRenderer = new WiredRenderer(lineRasterizer);

        panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        initScene();

        panel.requestFocus();
        panel.requestFocusInWindow();

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

//                if(e.getKeyCode() == KeyEvent.VK_LEFT)
//                    camera = camera.left(0.1);
//                if(e.getKeyCode() == KeyEvent.VK_RIGHT)
//                    camera = camera.right(0.1);
//                if(e.getKeyCode() == KeyEvent.VK_UP)
//                    camera = camera.forward(0.1);
//                if(e.getKeyCode() == KeyEvent.VK_DOWN)
//                    camera = camera.backward(0.1);

                if(e.getKeyChar() == 'p' || e.getKeyChar() == 'P') {
                    perspectiveProjection = !perspectiveProjection;
                    setProjectionMatrix();
                }

                if(e.getKeyChar() == 'S') {
                    if (cubeMode) {
                        cube.setModel(cube.getModel().mul(new Mat4Scale(1.1, 1.1, 1.1)));
                    } else {
                        pyramid.setModel(cube.getModel().mul(new Mat4Scale(1.1, 1.1, 1.1)));
                    }
                }
                if(e.getKeyChar() == 's') {
                    if (cubeMode) {
                        cube.setModel(cube.getModel().mul(new Mat4Scale(0.9, 0.9, 0.9)));
                    } else {
                        pyramid.setModel(cube.getModel().mul(new Mat4Scale(0.9, 0.9, 0.9)));
                    }
                }

                if(e.getKeyCode() == KeyEvent.VK_UP) {
                    if (cubeMode) {
                        cube.setModel(cube.getModel().mul(new Mat4Transl(transl, 0, 0)));
                    } else {
                        pyramid.setModel(cube.getModel().mul(new Mat4Transl(transl, 0, 0)));
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (cubeMode) {
                        cube.setModel(cube.getModel().mul(new Mat4Transl(-transl, 0, 0)));
                    } else {
                        pyramid.setModel(cube.getModel().mul(new Mat4Transl(-transl, 0, 0)));
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (cubeMode) {
                        cube.setModel(cube.getModel().mul(new Mat4Transl(0, transl, 0)));
                    } else {
                        pyramid.setModel(cube.getModel().mul(new Mat4Transl(0, transl, 0)));
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (cubeMode) {
                        cube.setModel(cube.getModel().mul(new Mat4Transl(0, -transl, 0)));
                    } else {
                        pyramid.setModel(cube.getModel().mul(new Mat4Transl(0, -transl, 0)));
                    }
                }

                renderScene();
            }
        });
    }

    public void initScene() {
        camera = new Camera(
                new Vec3D(0.5,-2,0.5),
                Math.toRadians(90),
                Math.toRadians(0),
                1. ,
                true
        );

        setProjectionMatrix();

        lineX = new Line(Color.RED, new Point3D(0, 0, 0), new Point3D(1, 0, 0));
        lineY = new Line(Color.GREEN, new Point3D(0, 0, 0), new Point3D(0, 1, 0));
        lineZ = new Line(Color.BLUE, new Point3D(0, 0, 0), new Point3D(0, 0, 1));

        cube = new Cube();
        pyramid = new Pyramid();
    }

    public void renderScene() {
        clear(0x000000);

        wiredRenderer.setView(camera.getViewMatrix());
        wiredRenderer.setProj(projection);
        wiredRenderer.renderScene(lineX, lineY, lineZ, cube, pyramid);

        panel.repaint();
    }

    public void clear(int color) {
        raster.setClearColor(color);
        raster.clear();
    }

    public void present(Graphics graphics) {
        raster.repaint(graphics);
    }

    public void start() {
        renderScene();
    }

    public void setProjectionMatrix() {
        if (perspectiveProjection) {
            projection = new Mat4PerspRH(
                    Math.PI / 4,
                    600 / 800.,
                    0.1,
                    20.
            );
        } else {
            projection = new Mat4OrthoRH(
                    800,
                    600,
                    0.1,
                    20
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Canvas3D(800, 600).start());
    }

}
