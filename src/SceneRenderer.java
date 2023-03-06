import objectOps.Renderer;
import object_data.*;
import raster_data.ColorRaster;
import raster_data.ZBuffer;
import transforms.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * Github: https://github.com/MartmatiX/PGRF2_2023
 */

public class SceneRenderer {

    private final JFrame frame;

    private final JPanel panel;
    private final ColorRaster img;

    private ZBuffer zBuffer;
    private Renderer renderer;
    private final Scene scene = new Scene();

    Camera camera = new Camera(new Vec3D(-10, 10, 5), 0, 0, 1, true);
    private final Double CAMERA_SPEED = 1d;

    private Point2D mousePos;

    private boolean isWired = false;

    private final AxisRGB axisRGB = new AxisRGB();

    private final Mat4Transl arrowMat = new Mat4Transl(1, 1, 1);
    private final Mat4Transl prismMat = new Mat4Transl(1, 10, 1);
    private final Mat4Transl octahedronMat = new Mat4Transl(1, 10, 8);

    private int selectedSolid = 1;
    private final ArrayList<Mat4Transl> solidMats = new ArrayList<>(List.of(arrowMat, prismMat, octahedronMat));

    private Mat4RotXYZ spinningPrismMat = new Mat4RotXYZ(10, 10, 10);
    private double gammaRotation = 1;

    public SceneRenderer(int width, int height) {
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        img = new ColorRaster(width, height, new Col(0, 0, 0));

        panel = new JPanel() {

            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                img.present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        JLabel controls = new JLabel("<html>"
                + "Movement: WASD QE<br/>"
                + "Translate solid: 8624 79 <br/>"
                + "Look around: Left Mouse Button <br/>"
                + "Wired models: V<br/>"
                + "Select Arrow: I"
                + "Select Octahedron: O<br/>"
                + "Select Prism: P<br/>"
                + "Exit: ESC <br/>"
                + "</html>");
        controls.setForeground(new Color(255, 255, 255));
        panel.add(controls, BorderLayout.WEST);

        System.out.println("""
                Controls:
                Movement: WASD QE
                Look around: Left Mouse Button
                Translate solid: 8624 79
                Wired models: V
                Select Arrow: I
                Select Octahedron: O
                Select Prism: P
                Exit: ESC
                """);

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    public void start() {
        zBuffer = new ZBuffer(img);
        renderer = new Renderer(zBuffer);

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> camera = camera.up(CAMERA_SPEED);
                    case KeyEvent.VK_S -> camera = camera.down(CAMERA_SPEED);
                    case KeyEvent.VK_A -> camera = camera.left(CAMERA_SPEED);
                    case KeyEvent.VK_D -> camera = camera.right(CAMERA_SPEED);
                    case KeyEvent.VK_E -> camera = camera.forward(CAMERA_SPEED);
                    case KeyEvent.VK_Q -> camera = camera.backward(CAMERA_SPEED);
                    case KeyEvent.VK_I -> selectedSolid = 0;
                    case KeyEvent.VK_P -> selectedSolid = 1;
                    case KeyEvent.VK_O -> selectedSolid = 2;
                    case KeyEvent.VK_V -> {
                        isWired = !isWired;
                    }
                    case KeyEvent.VK_ESCAPE -> {
                        System.out.println("Goodbye!\n");
                        System.exit(0);
                    }
                    case KeyEvent.VK_NUMPAD8 -> {
                        solidMats.set(selectedSolid, moveObject(1));
                    }
                    case KeyEvent.VK_NUMPAD6 -> {
                        solidMats.set(selectedSolid, moveObject(2));
                    }
                    case KeyEvent.VK_NUMPAD2 -> {
                        solidMats.set(selectedSolid, moveObject(3));
                    }
                    case KeyEvent.VK_NUMPAD4 -> {
                        solidMats.set(selectedSolid, moveObject(4));
                    }
                    case KeyEvent.VK_NUMPAD9 -> {
                        solidMats.set(selectedSolid, moveObject(9));
                    }
                    case KeyEvent.VK_NUMPAD7 -> {
                        solidMats.set(selectedSolid, moveObject(7));
                    }
                }
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                mousePos = new Point2D(e.getX(), e.getY());
                panel.addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent f) {
                        super.mouseDragged(f);
                        double dx = f.getX() - mousePos.getX();
                        double dy = f.getY() - mousePos.getY();

                        camera = camera.addAzimuth(-(dx) * Math.PI / 360);
                        camera = camera.addZenith(-(dy) * Math.PI / 360);

                        mousePos = new Point2D(f.getX(), f.getY());
                        render();
                    }
                });
            }
        });

        final double ZOOM_MODIFIER = 1.2;
        final double UNZOOM_MODIFIER = -1.2;

        panel.addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) {
                camera = camera.move(camera.getViewVector().mul(ZOOM_MODIFIER));
            } else {
                camera = camera.move(camera.getViewVector().mul(UNZOOM_MODIFIER));
            }
            render();
        });

        render();

        Runnable renderSpinningPrism = () -> {
            spinningPrismMat = new Mat4RotXYZ(spinningPrismMat.get(3, 0), spinningPrismMat.get(3, 1), spinningPrismMat.get(3,2) + gammaRotation);
            returnSolids();
        };

        Thread thread = new Thread(() -> {
           while (true){
               renderSpinningPrism.run();
               gammaRotation += 0.01;
               try {
                   Thread.sleep(1000 / 60);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }
        });
        thread.start();
    }

    public void render() {
        zBuffer.clear();
        renderer.drawScene(scene, camera.getViewMatrix(), new Mat4PerspRH(Math.PI / 2, (double) zBuffer.getColRaster().getHeight() / zBuffer.getColRaster().getWidth(), 0.1, 200));
        img.present(panel.getGraphics());
    }

    public Mat4Transl moveObject(int direction) {
        Mat4Transl originalMatrix = solidMats.get(selectedSolid);

        switch (direction) {
            case 1 -> {
                return new Mat4Transl(originalMatrix.get(3, 0), originalMatrix.get(3, 1), originalMatrix.get(3, 2) + 1);
            }
            case 2 -> {
                return new Mat4Transl(originalMatrix.get(3, 0), originalMatrix.get(3, 1) - 1, originalMatrix.get(3, 2));
            }
            case 3 -> {
                return new Mat4Transl(originalMatrix.get(3, 0), originalMatrix.get(3, 1), originalMatrix.get(3, 2) - 1);
            }
            case 4 -> {
                return new Mat4Transl(originalMatrix.get(3, 0), originalMatrix.get(3, 1) + 1, originalMatrix.get(3, 2));
            }
            case 7 -> {
                return new Mat4Transl(originalMatrix.get(3, 0) - 1, originalMatrix.get(3, 1), originalMatrix.get(3, 2));
            }
            case 9 -> {
                return new Mat4Transl(originalMatrix.get(3, 0) + 1, originalMatrix.get(3, 1), originalMatrix.get(3, 2));
            }
            default -> {
                return new Mat4Transl(0, 0, 0);
            }
        }
    }

    public void returnSolids(){
        scene.clearScene();
        scene.addSolid(axisRGB, new Mat4Scale(2).mul(new Mat4Transl(0, 0, 0)));

        Arrow arrow = new Arrow(isWired);
        scene.addSolid(arrow, new Mat4Scale(10).mul(solidMats.get(0)));

        Prism prism = new Prism(isWired);
        scene.addSolid(prism, new Mat4Scale(10).mul(solidMats.get(1)));

        Octahedron octahedron = new Octahedron(isWired);
        scene.addSolid(octahedron, new Mat4Scale(14).mul(solidMats.get(2)));

        Prism prismSpin = new Prism(isWired);
        scene.addSolid(prismSpin, new Mat4Scale(10).mul(new Mat4Transl(0, 0, 0).mul(spinningPrismMat)));

        render();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SceneRenderer(800, 600).start());
    }


}
