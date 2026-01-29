package com.example.controller;

import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;

import com.example.model.Scene;
import com.example.raster.Raster;
import com.example.rasterize.LineRasterizer;
import com.example.rasterize.LineRasterizerGraphics;
import com.example.renderer.Renderer;
import com.example.transforms.Camera;
import com.example.transforms.Mat4;
import com.example.transforms.Mat4OrthoRH;
import com.example.transforms.Mat4PerspRH;
import com.example.transforms.Vec3D;
import com.example.view.Panel;

public class Controller3D implements Controller {

    // Movement speed
    private static final double MOVE_SPEED = 0.2;
    private static final double ROTATE_SPEED = 0.01;

    private final Panel panel;
    private LineRasterizer lineRasterizer;
    private Scene scene;

    private Renderer renderer;

    // Camera
    private Camera camera;

    // Mouse handling
    private int lastMouseX, lastMouseY;
    private boolean mousePressed = false;

    // Projection matrixes
    private boolean perspectiveProjection = true;
    private Mat4 projectionMatrix;

    // Active solid index (starting at 0,1 are axes)
    private int activeSolidIndex = 1;

    /**
     * Creates a new 2D controller for the given panel.
     * 
     * @param panel Panel used for rendering and input handling
     */
    public Controller3D(Panel panel) {
        this.panel = panel;

        initObjects(panel.getRaster());
        initListeners(panel);

        render();
    }

    /**
     * Initializes rasterizers and drawable objects.
     *
     * @param raster Raster used for drawing operations
     */
    public void initObjects(Raster raster) {
        lineRasterizer = new LineRasterizerGraphics(raster);
        this.scene = new Scene();

        initCamera();
        initProjection();
        initScene();

        renderer = new Renderer(
                lineRasterizer,
                panel.getRaster().getWidth(),
                panel.getRaster().getHeight(),
                camera.getViewMatrix(),
                projectionMatrix);
    }

    private void initCamera() {
        camera = new Camera()
                .withPosition(new Vec3D(6, 6, 3))
                .withAzimuth(Math.toRadians(150))
                .withZenith(Math.toRadians(-25));
    }

    private void initProjection() {
        double height = Math.max(1, panel.getRaster().getHeight());
        double width = Math.max(1, panel.getRaster().getWidth());

        if (perspectiveProjection) {
            projectionMatrix = new Mat4PerspRH(
                    Math.toRadians(60),
                    height / width,
                    0.1,
                    100);
        } else {
            projectionMatrix = new Mat4OrthoRH((width / height) * 10,
                    10,
                    0.1,
                    100);
        }
    }

    private void initScene() {
        scene.clear();
        // axes
        // scene.addSolid(new Axis(0.5));

        // cube
        // scene.addSolid(cube);

        // Pyramid
        // scene.addSolid(pyramid);

        // Cylinder
        // scene.addSolid(cylinder);

        // Bezier curve
        // scene.addSolid(bezier);

        // Ferguson curve
        // scene.addSolid(ferguson);

        // Coons curve
        // scene.addSolid(coons);
    }

    @Override
    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }
        });

        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    // Cam up
                    case KeyEvent.VK_UP:
                        camera.forward(MOVE_SPEED);
                        break;
                    // Cam down
                    case KeyEvent.VK_DOWN:
                        camera.backward(MOVE_SPEED);
                        break;
                    // Cam left
                    case KeyEvent.VK_LEFT:
                        camera.left(ROTATE_SPEED);
                        break;
                    // Cam right
                    case KeyEvent.VK_RIGHT:
                        camera.right(ROTATE_SPEED);
                        break;
                    // Change projection mode
                    case KeyEvent.VK_P:
                        perspectiveProjection = !perspectiveProjection;
                        initProjection();
                        break;
                    // Cam up
                    case KeyEvent.VK_U:
                        camera = camera.up(MOVE_SPEED);
                        break;
                    // Cam down
                    case KeyEvent.VK_D:
                        camera = camera.down(MOVE_SPEED);
                        break;
                    // Reset camera
                    case KeyEvent.VK_R:
                        initCamera();
                        break;
                    // Next solid
                    case KeyEvent.VK_TAB:
                        activeSolidIndex++;
                        if (activeSolidIndex >= scene.getSolids().size()) {
                            activeSolidIndex = 1;
                        }
                    default:
                        break;
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                panel.resize();
                initObjects(panel.getRaster());
                render();
            }
        });
    }

    private void render() {
        panel.getRaster().clear();

        renderer = new Renderer(
                lineRasterizer,
                panel.getRaster().getWidth(),
                panel.getRaster().getHeight(),
                camera.getViewMatrix(),
                projectionMatrix);

        for (int i = 0; i < scene.getSolids().size(); i++) {
            renderer.renderSolid(scene.getSolids().get(i), i == activeSolidIndex);
        }

        panel.repaint();
    }
}
