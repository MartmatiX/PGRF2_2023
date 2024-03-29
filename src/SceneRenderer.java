import objectOps.Renderer;
import object_data.*;
import object_data.solids.*;
import object_data.solids.BicubicProcessor;
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

    Camera camera = new Camera(new Vec3D(-10, 2, 3), 0, 0, 1, true);
    private final double CAMERA_SPEED = 0.05;

    private Point2D mousePos;

    private boolean isWired = false;

    private final AxisRGB axisRGB = new AxisRGB();

    private final Mat4Transl arrowMat = new Mat4Transl(1, 1, 1); // 0
    private final Mat4Transl prismMat = new Mat4Transl(1, 10, 1); // 1
    private final Mat4Transl octahedronMat = new Mat4Transl(1, 10, 8); // 2
    private final Mat4Transl bezierMat = new Mat4Transl(0, 10, -3); // 3
    private final Mat4Transl fergusonMat = new Mat4Transl(0, 0, -3); // 4
    private final Mat4Transl coonsMat = new Mat4Transl(0, -10, -3); // 5
    private final Mat4Transl cubeMat = new Mat4Transl(0, -10, 0); // 6

    private final Mat4Rot arrowSpinMat = new Mat4Rot(0, 0, 0, 0);
    private final Mat4Rot prismSpinMat = new Mat4Rot(0, 0, 0, 0);
    private final Mat4Rot octahedronSpinMat = new Mat4Rot(0, 0, 0, 0);

    private int selectedSolid = 1;
    private final ArrayList<Mat4Transl> solidMats = new ArrayList<>(List.of(arrowMat, prismMat, octahedronMat, bezierMat, fergusonMat, coonsMat, cubeMat));
    private final ArrayList<Mat4> solidSpinMats = new ArrayList<>(List.of(arrowSpinMat, prismSpinMat, octahedronSpinMat));

    private Mat4RotXYZ spinningPrismMat = new Mat4RotXYZ(10, 10, 10);
    private double gammaRotation = 1;

    private boolean solidMoveSpin = true;

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

        JLabel controls = new JLabel("<html>" + "Movement: WASD QE<br/>" + "Translate solid: Numpad - 8624 79 <br/>" + "Switch to rotation mode: C<br/>" + "Rotate around Axis: 8624 79<br/>" + "Look around: Left Mouse Button <br/>" + "Wired models: V<br/>" + "Select Arrow: 1" + "Select Octahedron: 2<br/>" + "Select Prism: 3<br/>" + "Select bicubic: 4, 5, 6<br/>" + "Select Cube: 7<br/>" + "Exit: ESC <br/>" + "</html>");
        controls.setForeground(new Color(255, 255, 255));
        panel.add(controls, BorderLayout.WEST);

        System.out.println("""
                Controls:
                Movement: WASD QE
                Look around: Left Mouse Button
                Translate solid: Numpad - 8624 79
                Switch to rotation mode: C
                Rotate around Axis: 8624 79
                Wired models: V
                Select Arrow: 1
                Select Octahedron: 2
                Select Prism: 3
                Select Bicubic: 4, 5, 6
                Select Cube: 7
                Exit: ESC
                """);

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    public void start() {
        zBuffer = new ZBuffer(img);
        renderer = new Renderer(zBuffer);

        Runnable movement = () -> panel.addKeyListener(new KeyAdapter() {
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
                }
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_C -> solidMoveSpin = !solidMoveSpin;
                    case KeyEvent.VK_1 -> selectedSolid = 0;
                    case KeyEvent.VK_2 -> selectedSolid = 1;
                    case KeyEvent.VK_3 -> selectedSolid = 2;
                    case KeyEvent.VK_4 -> selectedSolid = 3;
                    case KeyEvent.VK_5 -> selectedSolid = 4;
                    case KeyEvent.VK_6 -> selectedSolid = 5;
                    case KeyEvent.VK_7 -> selectedSolid = 6;
                    case KeyEvent.VK_V -> isWired = !isWired;
                    case KeyEvent.VK_ESCAPE -> {
                        System.out.println("Goodbye!\n");
                        System.exit(0);
                    }
                }
            }
        });
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (solidMoveSpin) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_NUMPAD8 -> solidMats.set(selectedSolid, moveObject(1));
                        case KeyEvent.VK_NUMPAD6 -> solidMats.set(selectedSolid, moveObject(2));
                        case KeyEvent.VK_NUMPAD2 -> solidMats.set(selectedSolid, moveObject(3));
                        case KeyEvent.VK_NUMPAD4 -> solidMats.set(selectedSolid, moveObject(4));
                        case KeyEvent.VK_NUMPAD9 -> solidMats.set(selectedSolid, moveObject(9));
                        case KeyEvent.VK_NUMPAD7 -> solidMats.set(selectedSolid, moveObject(7));
                    }
                } else {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_NUMPAD6 -> solidSpinMats.set(selectedSolid, spinObject(11));
                        case KeyEvent.VK_NUMPAD4 -> solidSpinMats.set(selectedSolid, spinObject(22));
                        case KeyEvent.VK_NUMPAD8 -> solidSpinMats.set(selectedSolid, spinObject(33));
                        case KeyEvent.VK_NUMPAD2 -> solidSpinMats.set(selectedSolid, spinObject(44));
                        case KeyEvent.VK_NUMPAD7 -> solidSpinMats.set(selectedSolid, spinObject(55));
                        case KeyEvent.VK_NUMPAD9 -> solidSpinMats.set(selectedSolid, spinObject(66));
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
            spinningPrismMat = new Mat4RotXYZ(spinningPrismMat.get(3, 0), spinningPrismMat.get(3, 1), spinningPrismMat.get(3, 2) + gammaRotation);
            returnSolids();
        };

        Thread thread = new Thread(() -> {
            while (true) {
                renderSpinningPrism.run();
                gammaRotation += 0.01;
                try {
                    Thread.sleep(1000 / 60);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread movementThread = new Thread(() -> {
            while (true) {
                movement.run();
                try {
                    Thread.sleep(100000 / 60);
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            }
        });

        movementThread.start();
        thread.start();

        panel.setFocusable(true);
        panel.grabFocus();
    }

    public void render() {
        zBuffer.clear();
        renderer.drawScene(scene, camera.getViewMatrix(), new Mat4PerspRH(Math.PI / 2, (double) zBuffer.getColRaster().getHeight() / zBuffer.getColRaster().getWidth(), 0.1, 200));
        img.present(panel.getGraphics());
    }

    public Mat4Transl moveObject(int direction) {
        Mat4Transl originalMatrix = solidMats.get(selectedSolid);
        final double moveSpeed = 1;

        switch (direction) {
            case 1 -> {
                return new Mat4Transl(originalMatrix.get(3, 0), originalMatrix.get(3, 1), originalMatrix.get(3, 2) + moveSpeed);
            }
            case 2 -> {
                return new Mat4Transl(originalMatrix.get(3, 0), originalMatrix.get(3, 1) - moveSpeed, originalMatrix.get(3, 2));
            }
            case 3 -> {
                return new Mat4Transl(originalMatrix.get(3, 0), originalMatrix.get(3, 1), originalMatrix.get(3, 2) - moveSpeed);
            }
            case 4 -> {
                return new Mat4Transl(originalMatrix.get(3, 0), originalMatrix.get(3, 1) + moveSpeed, originalMatrix.get(3, 2));
            }
            case 7 -> {
                return new Mat4Transl(originalMatrix.get(3, 0) - moveSpeed, originalMatrix.get(3, 1), originalMatrix.get(3, 2));
            }
            case 9 -> {
                return new Mat4Transl(originalMatrix.get(3, 0) + moveSpeed, originalMatrix.get(3, 1), originalMatrix.get(3, 2));
            }
            default -> {
                return new Mat4Transl(0, 0, 0);
            }
        }
    }

    public Mat4 spinObject(int direction) {
        Mat4 originalSpinMatrix = solidSpinMats.get(selectedSolid);
        final double spinSpeed = 0.05;

        switch (direction) {
            case 11 -> {
                return originalSpinMatrix.mul(new Mat4Rot(spinSpeed, 1, 0, 0));
            }
            case 22 -> {
                return originalSpinMatrix.mul(new Mat4Rot(-spinSpeed, 1, 0, 0));
            }
            case 33 -> {
                return originalSpinMatrix.mul(new Mat4Rot(spinSpeed, 0, 1, 0));
            }
            case 44 -> {
                return originalSpinMatrix.mul(new Mat4Rot(-spinSpeed, 0, 1, 0));
            }
            case 55 -> {
                return originalSpinMatrix.mul(new Mat4Rot(spinSpeed, 0, 0, 1));
            }
            case 66 -> {
                return originalSpinMatrix.mul(new Mat4Rot(-spinSpeed, 0, 0, 1));
            }
            default -> {
                return new Mat4Rot(0, 0, 0, 0);
            }
        }
    }

    public void returnSolids() {
        scene.clearScene();
        scene.addSolid(axisRGB, new Mat4Scale(2).mul(new Mat4Transl(0, 0, 0)));

        Arrow arrow = new Arrow();
        scene.addSolid(arrow, new Mat4Scale(10).mul(solidMats.get(0)).mul(solidSpinMats.get(0)));

        Prism prism = new Prism(isWired);
        scene.addSolid(prism, new Mat4Scale(10).mul(solidMats.get(1)).mul(solidSpinMats.get(1)));

        Octahedron octahedron = new Octahedron(isWired);
        scene.addSolid(octahedron, new Mat4Scale(14).mul(solidMats.get(2)).mul(solidSpinMats.get(2)));

        Prism prismSpin = new Prism(isWired);
        scene.addSolid(prismSpin, new Mat4Scale(10).mul(new Mat4Transl(0, 0, 0).add(spinningPrismMat)));

        scene.addSolid(new BicubicProcessor(Cubic.BEZIER, new Col(0, 255, 255)), new Mat4Scale(5).mul(solidMats.get(3)));
        scene.addSolid(new BicubicProcessor(Cubic.FERGUSON, new Col(255, 255, 0)), new Mat4Scale(5).mul(solidMats.get(4)));
        scene.addSolid(new BicubicProcessor(Cubic.COONS, new Col(255, 0, 255)), new Mat4Scale(10).mul(solidMats.get(5)));

        scene.addSolid(new Cube(isWired), new Mat4Scale(3).mul(solidMats.get(6)));

        render();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SceneRenderer(800, 600).start());
    }


}
