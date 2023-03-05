import objectOps.Renderer;
import object_data.AxisRGB;
import object_data.Scene;
import raster_data.ColorRaster;
import raster_data.ZBuffer;
import transforms.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
                + "</html>");
        controls.setForeground(new Color(255, 255, 255));
        panel.add(controls, BorderLayout.WEST);

        System.out.println("""
                Controls:
                Movement: WASD QE
                """);

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    public void start() {
        zBuffer = new ZBuffer(img);
        renderer = new Renderer(zBuffer);

        scene.addSolid(new AxisRGB(), new Mat4Scale(5));

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
