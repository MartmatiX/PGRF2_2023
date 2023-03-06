import objectOps.Renderer;
import object_data.Arrow;
import object_data.AxisRGB;
import object_data.Prism;
import object_data.Scene;
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
                + "Movement: WASD QE</br>"
                + "Exit: ESC </br>"
                + "</html>");
        controls.setForeground(new Color(255, 255, 255));
        panel.add(controls, BorderLayout.WEST);

        System.out.println("""
                Controls:
                Movement: WASD QE
                Exit: ESC
                """);

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    public void start() {
        zBuffer = new ZBuffer(img);
        renderer = new Renderer(zBuffer);

        scene.addSolid(new AxisRGB(), new Mat4Scale(2));
        scene.addSolid(new Arrow(), new Mat4Scale(10).mul(new Mat4Transl(1, 1, 1)));
        scene.addSolid(new Prism(), new Mat4Scale(10).mul(new Mat4Transl(1, 10, 1)));

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
                    case KeyEvent.VK_ESCAPE -> {
                        System.out.println("Goodbye!\n");
                        System.exit(0);
                    }
                }
                render();
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
            if (e.getWheelRotation() < 0){
                camera = camera.move(camera.getViewVector().mul(ZOOM_MODIFIER));
            } else {
                camera = camera.move(camera.getViewVector().mul(UNZOOM_MODIFIER));
            }
            render();
        });

        render();
    }

    public void render() {
        zBuffer.clear();
        renderer.drawScene(scene, camera.getViewMatrix(), new Mat4PerspRH(Math.PI / 2, (double) zBuffer.getColRaster().getHeight() / zBuffer.getColRaster().getWidth(), 0.1, 200));
        img.present(panel.getGraphics());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SceneRenderer(800, 600).start());
    }


}
