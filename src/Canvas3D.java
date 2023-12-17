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
import java.util.ArrayList;

public class Canvas3D {
    private JPanel panel;
    private RasterBufferedImage raster;
    private LineRasterizer lineRasterizer;
    private WiredRenderer wiredRenderer;
    private Solid pyramid, cube, lineX, lineY, lineZ;
    private ArrayList<Solid> sin, cos;
    private Camera camera;
    private Mat4 projection;
    private double transl = 0.1;
    private int x, y;
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

                // Camera movements
                if(e.getKeyCode() == KeyEvent.VK_A)
                    camera = camera.left(0.1);
                if(e.getKeyCode() == KeyEvent.VK_D)
                    camera = camera.right(0.1);
                if(e.getKeyCode() == KeyEvent.VK_W)
                    camera = camera.forward(0.1);
                if(e.getKeyCode() == KeyEvent.VK_S)
                    camera = camera.backward(0.1);
                if(e.getKeyCode() == KeyEvent.VK_SPACE && !e.isShiftDown())
                    camera = camera.up(0.1);
                if(e.getKeyCode() == KeyEvent.VK_CONTROL && !e.isShiftDown())
                    camera = camera.down(0.1);


                // Change projection matrix
                if(e.getKeyChar() == 'p' || e.getKeyChar() == 'P') {
                    perspectiveProjection = !perspectiveProjection;
                    setProjectionMatrix();
                }

                // Scale solids up and down
                if((e.getKeyChar() == 'Q' || e.getKeyChar() == 'q') && !e.isShiftDown()) {
                    if (cubeMode) {
                        cube.setModel(cube.getModel().mul(new Mat4Scale(1.1, 1.1, 1.1)));
                    } else {
                        pyramid.setModel(pyramid.getModel().mul(new Mat4Scale(1.1, 1.1, 1.1)));
                    }
                }
                if((e.getKeyChar() == 'Q' || e.getKeyChar() == 'q') && e.isShiftDown()) {
                    if (cubeMode) {
                        cube.setModel(cube.getModel().mul(new Mat4Scale(0.9, 0.9, 0.9)));
                    } else {
                        pyramid.setModel(pyramid.getModel().mul(new Mat4Scale(0.9, 0.9, 0.9)));
                    }
                }

                // Translate solids
                // Translate on x-axis
                if(e.getKeyCode() == KeyEvent.VK_UP) {
                    if (cubeMode) {
                        cube.setModel(cube.getModel().mul(new Mat4Transl(transl, 0, 0)));
                    } else {
                        pyramid.setModel(pyramid.getModel().mul(new Mat4Transl(transl, 0, 0)));
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (cubeMode) {
                        cube.setModel(cube.getModel().mul(new Mat4Transl(-transl, 0, 0)));
                    } else {
                        pyramid.setModel(pyramid.getModel().mul(new Mat4Transl(-transl, 0, 0)));
                    }
                }

                // Translate on y-axis
                if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (cubeMode) {
                        cube.setModel(cube.getModel().mul(new Mat4Transl(0, transl, 0)));
                    } else {
                        pyramid.setModel(pyramid.getModel().mul(new Mat4Transl(0, transl, 0)));
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (cubeMode) {
                        cube.setModel(cube.getModel().mul(new Mat4Transl(0, -transl, 0)));
                    } else {
                        pyramid.setModel(pyramid.getModel().mul(new Mat4Transl(0, -transl, 0)));
                    }
                }

                // Translate on z-axis
                if(e.getKeyCode() == KeyEvent.VK_SPACE && e.isShiftDown()) {
                    if (cubeMode) {
                        cube.setModel(cube.getModel().mul(new Mat4Transl(0, 0, transl)));
                    } else {
                        pyramid.setModel(pyramid.getModel().mul(new Mat4Transl(0, 0, transl)));
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_CONTROL && e.isShiftDown()) {
                    if (cubeMode) {
                        cube.setModel(cube.getModel().mul(new Mat4Transl(0, 0, -transl)));
                    } else {
                        pyramid.setModel(pyramid.getModel().mul(new Mat4Transl(0, 0, -transl)));
                    }
                }

                // Rotate solids
                // Rotate on z-axis
                if(e.getKeyCode() == 'z' || e.getKeyCode() == 'Z') {
                    if (!e.isShiftDown()) {
                        if (cubeMode) {
                            cube.setModel(cube.getModel().mul(new Mat4RotZ(Math.PI / 6)));
                        } else {
                            pyramid.setModel(pyramid.getModel().mul(new Mat4RotZ(Math.PI / 6)));
                        }
                    } else {
                        if (cubeMode) {
                            cube.setModel(cube.getModel().mul(new Mat4RotZ(-Math.PI / 6)));
                        } else {
                            pyramid.setModel(pyramid.getModel().mul(new Mat4RotZ(-Math.PI / 6)));
                        }
                    }
                }

                // Rotate on x-axis
                if(e.getKeyCode() == 'x' || e.getKeyCode() == 'X') {
                    if (!e.isShiftDown()) {
                        if (cubeMode) {
                            cube.setModel(cube.getModel().mul(new Mat4RotX(Math.PI / 6)));
                        } else {
                            pyramid.setModel(pyramid.getModel().mul(new Mat4RotX(Math.PI / 6)));
                        }
                    } else {
                        if (cubeMode) {
                            cube.setModel(cube.getModel().mul(new Mat4RotX(-Math.PI / 6)));
                        } else {
                            pyramid.setModel(pyramid.getModel().mul(new Mat4RotX(-Math.PI / 6)));
                        }
                    }
                }

                // Rotate on y-axis
                if(e.getKeyCode() == 'y' || e.getKeyCode() == 'Y') {
                    if (!e.isShiftDown()) {
                        if (cubeMode) {
                            cube.setModel(cube.getModel().mul(new Mat4RotY(Math.PI / 6)));
                        } else {
                            pyramid.setModel(pyramid.getModel().mul(new Mat4RotY(Math.PI / 6)));
                        }
                    } else {
                        if (cubeMode) {
                            cube.setModel(cube.getModel().mul(new Mat4RotY(-Math.PI / 6)));
                        } else {
                            pyramid.setModel(pyramid.getModel().mul(new Mat4RotY(-Math.PI / 6)));
                        }
                    }
                }

                if(e.getKeyCode() == 'm' || e.getKeyCode() == 'M') {
                    cubeMode = !cubeMode;
                }
                renderScene();
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                x = e.getX();
                y = e.getY();
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x2 = e.getX(), y2 = e.getY();

                final double dx = x - x2;
                final double dy = y - y2;

                x = x2;
                y = y2;

                camera = camera.addAzimuth(dx / panel.getWidth()).addZenith(dy / panel.getHeight());

                renderScene();
            }
        });
    }

    public void initScene() {
        camera = new Camera(
                new Vec3D(-2,1,1),
                Math.toRadians(-15),
                Math.toRadians(-15),
                1. ,
                true
        );

        setProjectionMatrix();

        lineX = new Line(Color.RED, new Point3D(0, 0, 0), new Point3D(1, 0, 0));
        lineY = new Line(Color.GREEN, new Point3D(0, 0, 0), new Point3D(0, 1, 0));
        lineZ = new Line(Color.BLUE, new Point3D(0, 0, 0), new Point3D(0, 0, 1));

        cube = new Cube();
        pyramid = new Pyramid();

        cos = createCosFunction();
        sin = createSinFunction();
    }

    public void renderScene() {
        clear(0x000000);

        wiredRenderer.setView(camera.getViewMatrix());
        wiredRenderer.setProj(projection);
        wiredRenderer.renderScene(lineX, lineY, lineZ, cube, pyramid);

        for (Solid line: cos) {
            wiredRenderer.renderScene(line);
        }

        for (Solid line: sin) {
            wiredRenderer.renderScene(line);
        }

        panel.repaint();
    }

    public ArrayList<Solid> createCosFunction() {
        ArrayList<Solid> graph = new ArrayList<>();
        for (int i = 0; i <= 10000; i++) {
            double x = i / 100.0;
            double y = Math.cos(x);
            double xNext = (i+1) / 100.0;
            double yNext = Math.cos(xNext);

            Line line = new Line(Color.CYAN, new Point3D(x, y, 0), new Point3D(xNext, yNext, 0));
            graph.add(line);
        }
        return graph;
    }

    public ArrayList<Solid> createSinFunction() {
        ArrayList<Solid> graph = new ArrayList<>();
        for (int i = 0; i <= 10000; i++) {
            double x = i / 100.0;
            double y = Math.sin(x);
            double xNext = (i+1) / 100.0;
            double yNext = Math.sin(xNext);

            Line line = new Line(Color.CYAN, new Point3D(x, y, 0), new Point3D(xNext, yNext, 0));
            graph.add(line);
        }
        return graph;
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
                    (double) 800 / 200,
                    (double) 600 / 200,
                    0.1,
                    20
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Canvas3D(800, 600).start());
    }

}
