package com.example.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

import javax.swing.SwingUtilities;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;

import com.example.enums.ColorMode;
import com.example.enums.FillColorMode;
import com.example.enums.FillTool;
import com.example.enums.RasterizerMode;
import com.example.enums.SolidAction;
import com.example.enums.SolidState;
import com.example.fill.ScanLine;
import com.example.fill.SeedFill;
import com.example.fill.SeedFillBorder;
import com.example.fill.SeedFiller;
import com.example.fill.SolidFiller;
import com.example.model.Scene;
import com.example.model.solid.Arrow;
import com.example.model.solid.AxisX;
import com.example.model.solid.AxisY;
import com.example.model.solid.AxisZ;
import com.example.model.solid.BezierCurve;
import com.example.model.solid.CoonsCurve;
import com.example.model.solid.FergusonCurve;
import com.example.model.solid.Solid;
import com.example.raster.Raster;
import com.example.raster.RasterBufferedImage;
import com.example.rasterize.FilledLineRasterizer;
import com.example.rasterize.LineRasterizer;
import com.example.renderer.Renderer;
import com.example.transforms.Camera;
import com.example.transforms.Col;
import com.example.transforms.Mat4;
import com.example.transforms.Mat4OrthoRH;
import com.example.transforms.Mat4PerspRH;
import com.example.transforms.Mat4RotX;
import com.example.transforms.Mat4RotY;
import com.example.transforms.Mat4RotZ;
import com.example.transforms.Mat4Scale;
import com.example.transforms.Mat4Transl;
import com.example.transforms.Point3D;
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

    // Active solid index (starting at 0, 1 are axes)
    private int activeSolidIndex = 0;

    private ColorMode colorMode;

    // TODO - scan-line (computing all solid polygons), cannot with seedfill and
    // seedfillborder refill pattern with solid color, seedfillborder in this state
    // cannot work with gradient edges
    // TODO - resize end and start point

    // TODO - implement another solid models (cube, pyramid, cylinder)

    // TODO - implement proofing for edges of solid models - check
    // TODO - implement rotation for every solid model - check

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
        colorMode = panel.getColorMode();

        lineRasterizer = new FilledLineRasterizer(raster);
        // lineRasterizer = new LineRasterizerGraphics(raster);

        lineRasterizer.setRasterizeMode(RasterizerMode.NORMAL);
        setRasterizerDrawingColor();

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
                .withPosition(new Vec3D(0.5, -1.5, 1.5))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-25))
                .withFirstPerson(true);
    }

    private void initProjection() {
        double height = Math.max(1, panel.getRaster().getHeight());
        double width = Math.max(1, panel.getRaster().getWidth());

        if (perspectiveProjection) {
            projectionMatrix = new Mat4PerspRH(
                    Math.toRadians(90),
                    width / height,
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
        AxisX axisX = new AxisX();
        AxisY axisY = new AxisY();
        AxisZ axisZ = new AxisZ();

        // each axis is length 5 units
        axisX.setModel(new Mat4Scale(5, 1, 1));
        axisY.setModel(new Mat4Scale(1, 5, 1));
        axisZ.setModel(new Mat4Scale(1, 1, 5));

        scene.addSolid(axisX);
        scene.addSolid(axisY);
        scene.addSolid(axisZ);

        // Arrow
        scene.addSolid(new Arrow());

        // cube
        // Cube cube = new Cube(2.0);
        // scene.addSolid(cube);

        // Pyramid
        // Pyramid pyramid = new Pyramid(2.0);
        // scene.addSolid(pyramid);

        // Cylinder
        // Cylinder cylinder = new Cylinder(1.0, 2.0, 32);
        // scene.addSolid(cylinder);

        // Bezier curve
        BezierCurve bezier = new BezierCurve(new Point3D[] {
                new Point3D(-1, 1, 2),
                new Point3D(1, -1, 2),
                new Point3D(-1, 1, 0),
                new Point3D(1, -1, 0) },
                100);
        bezier.compute();
        scene.addSolid(bezier);

        // Ferguson curve
        FergusonCurve ferguson = new FergusonCurve(new Point3D[] {
                new Point3D(-1, 1, 2),
                new Point3D(1, -1, 0),
                new Point3D(0, 0, -2),
                new Point3D(0, 0, -2) },
                100);
        ferguson.compute();
        scene.addSolid(ferguson);

        // Coons curve
        CoonsCurve coonsCurve = new CoonsCurve(new Point3D[] {
                new Point3D(-1, 1, 2),
                new Point3D(1, -1, 2),
                new Point3D(-1, 1, 0),
                new Point3D(1, -1, 0) },
                100);
        coonsCurve.compute();
        scene.addSolid(coonsCurve);
    }

    @Override
    public void initListeners(Panel panel) {
        panel.setFocusable(true);
        panel.setFocusTraversalKeysEnabled(false);

        panel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                Solid activeSolid = getActiveSolid();

                if (activeSolid == null) {
                    return;
                }

                double scale = (e.getUnitsToScroll() > 0) ? 1.1 : 0.9;
                activeSolid.setModel(new Mat4Scale(scale).mul(activeSolid.getModel()));

                render();
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    List<Col> setColors = lineRasterizer.getColors();
                    Raster ptRaster = createPatternRaster(100, 100);

                    /*
                     * Scan-line algorithm is more ralible with gradient then seedFill and
                     * seedFillBorder
                     */
                    // with pattern
                    if (panel.getFillTool() == FillTool.SCANLINE && panel.getFillColorMode() == FillColorMode.PATTERN) {
                        System.out.println("Used scan-line with pattern filling");
                        SolidFiller scanLine = new ScanLine(panel.getRaster(), ptRaster);
                        scanLine.fill(getActiveSolid(), e.getX(), e.getY());

                        update();
                        return;
                    }

                    // with color
                    if (panel.getFillTool() == FillTool.SCANLINE && panel.getFillColorMode() == FillColorMode.COLOR) {
                        System.out.println("Used scan-line with color filling");
                        SolidFiller scanLine = new ScanLine(panel.getRaster(), new Col(165, 42, 42, 255));
                        scanLine.fill(getActiveSolid(), e.getX(), e.getY());

                        update();
                        return;
                    }

                    // with pattern
                    if (panel.getFillTool() == FillTool.SEEDFILL && panel.getFillColorMode() == FillColorMode.PATTERN) {
                        System.out.println("Used seed fill with pattern filling");
                        SeedFiller seedFill = new SeedFill(panel.getRaster(), ptRaster,
                                panel.getRaster().getPixel(e.getX(), e.getY()), e.getX(), e.getY());
                        seedFill.fill();

                        update();
                        return;
                    }

                    // with color
                    if (panel.getFillTool() == FillTool.SEEDFILL && panel.getFillColorMode() == FillColorMode.COLOR) {
                        System.out.println("Used seed fill with color filling");
                        SeedFiller seedFill = new SeedFill(panel.getRaster(),
                                panel.getRaster().getPixel(e.getX(), e.getY()), new Col(165, 42, 42, 255),
                                e.getX(),
                                e.getY());
                        seedFill.fill();

                        update();
                        return;
                    }

                    // with pattern
                    if (panel.getFillTool() == FillTool.SEEDFILLBORDER
                            && panel.getFillColorMode() == FillColorMode.PATTERN) {
                        System.out.println("Used seed fill border with pattern filling");
                        SeedFiller seedFillBorder = new SeedFillBorder(panel.getRaster(), ptRaster,
                                setColors.get(0),
                                e.getX(), e.getY());
                        seedFillBorder.fill();

                        update();
                        return;
                    }

                    // with color
                    if (panel.getFillTool() == FillTool.SEEDFILLBORDER
                            && panel.getFillColorMode() == FillColorMode.COLOR) {
                        System.out.println("Used seed fill border with color filling");
                        SeedFiller seedFillBorder = new SeedFillBorder(panel.getRaster(),
                                setColors.get(0), new Col(165, 42, 42, 255), e.getX(),
                                e.getY());
                        seedFillBorder.fill();

                        update();
                        return;
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                panel.requestFocusInWindow();

                lastMouseX = e.getX();
                lastMouseY = e.getY();

                mousePressed = true;
            }

            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (mousePressed) {
                    int dx = e.getX() - lastMouseX;
                    int dy = e.getY() - lastMouseY;

                    camera = camera.addAzimuth(-dx * ROTATE_SPEED);
                    camera = camera.addZenith(-dy * ROTATE_SPEED);

                    lastMouseX = e.getX();
                    lastMouseY = e.getY();

                    render();
                }
            }
        });

        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                Solid activeSolid = getActiveSolid();

                if (activeSolid == null) {
                    return;
                }

                switch (e.getKeyCode()) {
                    // Cam up
                    case KeyEvent.VK_UP:
                        camera = camera.forward(MOVE_SPEED);
                        break;
                    // Cam down
                    case KeyEvent.VK_DOWN:
                        camera = camera.backward(MOVE_SPEED);
                        break;
                    // Cam left
                    case KeyEvent.VK_LEFT:
                        camera = camera.left(MOVE_SPEED);
                        break;
                    // Cam right
                    case KeyEvent.VK_RIGHT:
                        camera = camera.right(MOVE_SPEED);
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
                            activeSolidIndex = 0;
                        }
                        break;

                    // Move
                    // AXIS X
                    case KeyEvent.VK_Q:
                        updateSolid(activeSolid, SolidAction.PROOFING, new Mat4Transl(-0.1, 0, 0), null);
                        break;
                    case KeyEvent.VK_W:
                        updateSolid(activeSolid, SolidAction.PROOFING, new Mat4Transl(0.1, 0, 0), null);
                        break;

                    // AXIS Y
                    case KeyEvent.VK_A:
                        updateSolid(activeSolid, SolidAction.PROOFING, new Mat4Transl(0, -0.1, 0), null);
                        break;
                    case KeyEvent.VK_S:
                        updateSolid(activeSolid, SolidAction.PROOFING, new Mat4Transl(0, 0.1, 0), null);
                        break;

                    // AXIS Z
                    case KeyEvent.VK_C:
                        updateSolid(activeSolid, SolidAction.PROOFING, new Mat4Transl(0, 0, -0.1), null);
                        break;
                    case KeyEvent.VK_V:
                        updateSolid(activeSolid, SolidAction.PROOFING, new Mat4Transl(0, 0, 0.1), null);
                        break;

                    // Rotation
                    // AXIS X
                    case KeyEvent.VK_I:
                        updateSolid(activeSolid, SolidAction.ROTATION, null, new Mat4RotX(-0.1));
                        break;
                    case KeyEvent.VK_O:
                        updateSolid(activeSolid, SolidAction.ROTATION, null, new Mat4RotX(0.1));
                        break;

                    // AXIS Y
                    case KeyEvent.VK_K:
                        updateSolid(activeSolid, SolidAction.ROTATION, null, new Mat4RotY(-0.1));
                        break;
                    case KeyEvent.VK_L:
                        updateSolid(activeSolid, SolidAction.ROTATION, null, new Mat4RotY(0.1));
                        break;

                    // AXIS Z
                    case KeyEvent.VK_N:
                        updateSolid(activeSolid, SolidAction.ROTATION, null, new Mat4RotZ(-0.1));
                        break;
                    case KeyEvent.VK_M:
                        updateSolid(activeSolid, SolidAction.ROTATION, null, new Mat4RotZ(0.1));
                        break;
                    default:
                        break;
                }
                render();
            }

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.resize();
                initObjects(panel.getRaster());
            }
        });
    }

    private Solid getActiveSolid() {
        if (scene.getSolids().isEmpty())
            return null;

        if (activeSolidIndex >= scene.getSolids().size()) {
            activeSolidIndex = 0;
        }

        return scene.getSolids().get(activeSolidIndex);
    }

    private void updateSolid(Solid solid, SolidAction action, Mat4Transl translationValue,
            Object rotationValue) {

        if ((solid == null || action == null) || (translationValue == null && rotationValue == null))
            return;

        switch (action) {
            case ROTATION:
                if (rotationValue instanceof Mat4RotX) {
                    solid.setModel(((Mat4RotX) rotationValue).mul(solid.getModel()));
                }

                if (rotationValue instanceof Mat4RotY) {
                    solid.setModel(((Mat4RotY) rotationValue).mul(solid.getModel()));
                }

                if (rotationValue instanceof Mat4RotZ) {
                    solid.setModel(((Mat4RotZ) rotationValue).mul(solid.getModel()));
                }
                break;
            case PROOFING:
                solid.setModel(solid.getModel().mul(translationValue));
                break;
            default:
                break;
        }
    }

    private void render() {
        panel.clear();

        if (colorMode != panel.getColorMode()) {
            colorMode = panel.getColorMode();
            setRasterizerDrawingColor();
        }

        renderer.setView(camera.getViewMatrix());
        renderer.setProj(projectionMatrix);

        for (int i = 0; i < scene.getSolids().size(); i++) {
            if (i == activeSolidIndex)
                scene.getSolids().get(i).setState(SolidState.SELECTED);
            else
                scene.getSolids().get(i).setState(SolidState.NORMAL);

            renderer.renderSolid(scene.getSolids().get(i));
        }

        update();
    }

    private void setRasterizerDrawingColor() {
        if (colorMode == ColorMode.GRADIENT) {
            lineRasterizer.setColorMode(ColorMode.GRADIENT);
            lineRasterizer.setGradientColors(
                    new Col(255, 0, 0), // red
                    new Col(0, 0, 255) // blue
            );
        } else if (colorMode == ColorMode.SOLID) {
            lineRasterizer.setColorMode(ColorMode.SOLID);
            lineRasterizer.setSolidColor(new Col(0, 255, 0)); // green
        } else {
            lineRasterizer.setColorMode(ColorMode.SOLID);
            lineRasterizer.setSolidColor(new Col(0, 255, 0)); // green
        }
    }

    /**
     * Repaints the panel.
     */
    private void update() {
        panel.repaint();
    }

    private Raster createPatternRaster(int width, int height) {

        if (width < 0 || height < 0)
            return null;

        Raster patternRaster = new RasterBufferedImage(width, height);

        int lightGray = 0xDDDDDD;
        int darkGray = 0x777777;

        for (int py = 0; py < height; py++) {
            for (int px = 0; px < width; px++) {
                if ((px + py) % 2 == 0) {
                    patternRaster.setPixel(px, py, lightGray);
                } else {
                    patternRaster.setPixel(px, py, darkGray);
                }
            }
        }

        return patternRaster;
    }
}
