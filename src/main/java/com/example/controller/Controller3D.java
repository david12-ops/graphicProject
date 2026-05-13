package com.example.controller;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;

import com.example.enums.ColorDrawMode;
import com.example.enums.ColorFillMode;
import com.example.enums.SolidAction;
import com.example.enums.SolidModel;
import com.example.enums.SolidState;
import com.example.model.Light;
import com.example.model.Scene;
import com.example.model.Texture;
import com.example.model.Vertex;
import com.example.model.solid.AxisX;
import com.example.model.solid.AxisY;
import com.example.model.solid.AxisZ;
import com.example.model.solid.Cube;
import com.example.model.solid.Cylinder;
import com.example.model.solid.Round;
import com.example.model.solid.Solid;
import com.example.raster.ZBuffer;
import com.example.rasterize.FilledLineRasterizer;
import com.example.rasterize.LineRasterizer;
import com.example.rasterize.PointRasterizer;
import com.example.rasterize.TriangleRasterizer;
import com.example.renderer.Renderer;
import com.example.shader.PhongShader;
import com.example.shader.ShaderConstant;
import com.example.shader.ShaderInterpolated;
import com.example.shader.ShaderTexture;
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

    private Panel panel;
    private ZBuffer zBuffer;
    private LineRasterizer lineRasterizer;
    private TriangleRasterizer triangleRasterizer;
    private PointRasterizer pointRasterizer;
    private SolidModel solidModel = SolidModel.WIREFRAME;

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

    private int activeSolidIndex = 0;

    private ColorDrawMode colorDrawMode;
    private ColorFillMode colorFillMode;

    private final ShaderConstant shaderConstant = new ShaderConstant();
    private final ShaderInterpolated shaderInterpolated = new ShaderInterpolated();
    private PhongShader phongShader;

    // TODO - osvicení

    /**
     * Creates a new 3D controller for the given panel.
     * 
     * @param panel Panel used for rendering and input handling
     */
    public Controller3D(Panel panel) {
        this.panel = panel;

        initObjects();
        initListeners(panel);

        render();
    }

    /**
     * Initializes rasterizers and drawable objects.
     *
     * @param raster Raster used for drawing operations
     */
    public void initObjects() {
        colorDrawMode = panel.getColorDrawMode();
        colorFillMode = panel.getColorFillMode();

        zBuffer = new ZBuffer(panel.getRaster());

        lineRasterizer = new FilledLineRasterizer(zBuffer);
        triangleRasterizer = new TriangleRasterizer(zBuffer);
        pointRasterizer = new PointRasterizer(zBuffer);

        this.scene = new Scene();

        initCamera();
        initProjection();
        initScene(solidModel);

        phongShader = new PhongShader(scene.getSceneLight());
        phongShader.setCameraPosition(camera.getPosition());

        renderer = new Renderer(lineRasterizer, triangleRasterizer, pointRasterizer, scene.getSceneLight(),
                panel.getRaster().getWidth(),
                panel.getRaster().getHeight(), camera.getViewMatrix(), projectionMatrix);
    }

    private void initCamera() {
        camera = new Camera()
                .withPosition(new Vec3D(1.1, -1.5, 1.5))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-25))
                .withFirstPerson(true);
    }

    private void initProjection() {
        double height = Math.max(1, panel.getHeight());
        double width = Math.max(1, panel.getWidth());

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

    private void initScene(SolidModel solidModel) {
        scene.setSceneLight(new Light(
                new Point3D(10, 10, 10),
                new Col(255, 255, 255)));
        scene.clear();

        // axes
        AxisX axisX = new AxisX(new Col[] {
                new Col(255, 0, 0), // strong red
                new Col(255, 80, 80), // light red
                new Col(180, 0, 0) // dark red
        }, solidModel);

        AxisY axisY = new AxisY(new Col[] {
                new Col(0, 255, 0), // strong green
                new Col(120, 255, 120), // light green
                new Col(0, 140, 0) // dark green
        }, solidModel);

        AxisZ axisZ = new AxisZ(new Col[] {
                new Col(0, 120, 255), // strong blue
                new Col(120, 190, 255), // light blue
                new Col(0, 60, 180) // dark blue
        }, solidModel);

        axisX.computeCenter();
        axisY.computeCenter();
        axisZ.computeCenter();

        axisX.setUseModelMatrix(false);
        axisY.setUseModelMatrix(false);
        axisZ.setUseModelMatrix(false);

        scene.addSolid(axisX);
        scene.addSolid(axisY);
        scene.addSolid(axisZ);

        // Cube
        Cube cube = new Cube(
                2.0,
                new Col[] {
                        new Col(255, 0, 0),
                        new Col(0, 255, 0),
                        new Col(0, 0, 255)
                },
                solidModel);

        cube.setModel(new Mat4Transl(3, 6, 0));
        cube.computeCenter();

        try {
            Texture cubeTexture = new Texture("/beasternchen-bee-9766784.jpg");
            cube.setTexture(cubeTexture);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("File was not load properly");
        }

        scene.addSolid(cube);

        // Cylinder
        Cylinder cylinder = new Cylinder(1.0, 2.0, 32, new Col[] {
                new Col(255, 165, 0), // orange
                new Col(128, 0, 128), // purple
                new Col(0, 255, 255) // cyan
        }, solidModel);

        cylinder.setModel(new Mat4Transl(-1.5, 6, 0));
        cylinder.computeCenter();

        try {
            Texture cubeTexture = new Texture("/jplenio-ocean-3605547.jpg");
            cylinder.setTexture(cubeTexture);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("File was not load properly");
        }

        scene.addSolid(cylinder);

        // Round
        Round round = new Round(
                new Point3D(0, 0, 0),
                2,
                16,
                32,
                new Col[] {
                        new Col(139, 69, 19), // brown
                        new Col(25, 25, 112), // midnight blue
                        new Col(47, 79, 79) // dark slate gray
                },
                solidModel);

        round.setModel(new Mat4Transl(10, 6, 5));
        round.setUsePongShader(true);
        round.computeCenter();

        try {
            Texture cubeTexture = new Texture("/pruslee-plane-7432680.jpg");
            round.setTexture(cubeTexture);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("File was not load properly");
        }

        scene.addSolid(round);
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
                Vec3D centerVec3d = getCenterVec(activeSolid.getVertexBuffer());

                Mat4 scaleAroundCenter = new Mat4Transl(centerVec3d.opposite()).mul(new Mat4Scale(scale))
                        .mul(new Mat4Transl(centerVec3d));

                activeSolid.setModel(scaleAroundCenter.mul(activeSolid.getModel()));

                render();
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                panel.requestFocusInWindow();

                lastMouseX = e.getX();
                lastMouseY = e.getY();

                mousePressed = true;

                phongShader.setCameraPosition(camera.getPosition());
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

                    phongShader.setCameraPosition(camera.getPosition());

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
                    case KeyEvent.VK_M:
                        if (solidModel == SolidModel.WIREFRAME) {
                            solidModel = SolidModel.SOLID;
                        } else {
                            solidModel = SolidModel.WIREFRAME;
                        }

                        initScene(solidModel);
                        render();
                        break;
                    // Cam up
                    case KeyEvent.VK_UP:
                        camera = camera.forward(MOVE_SPEED);
                        phongShader.setCameraPosition(camera.getPosition());
                        break;
                    // Cam down
                    case KeyEvent.VK_DOWN:
                        camera = camera.backward(MOVE_SPEED);
                        phongShader.setCameraPosition(camera.getPosition());
                        break;
                    // Cam left
                    case KeyEvent.VK_LEFT:
                        camera = camera.left(MOVE_SPEED);
                        phongShader.setCameraPosition(camera.getPosition());
                        break;
                    // Cam right
                    case KeyEvent.VK_RIGHT:
                        camera = camera.right(MOVE_SPEED);
                        phongShader.setCameraPosition(camera.getPosition());
                        break;
                    // Change projection mode
                    case KeyEvent.VK_P:
                        perspectiveProjection = !perspectiveProjection;
                        initProjection();
                        phongShader.setCameraPosition(camera.getPosition());

                        break;
                    // Cam up
                    case KeyEvent.VK_U:
                        camera = camera.up(MOVE_SPEED);
                        phongShader.setCameraPosition(camera.getPosition());
                        break;
                    // Cam down
                    case KeyEvent.VK_D:
                        camera = camera.down(MOVE_SPEED);
                        phongShader.setCameraPosition(camera.getPosition());
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
                    case KeyEvent.VK_H:
                        updateSolid(activeSolid, SolidAction.ROTATION, null, new Mat4RotZ(-0.1));
                        break;
                    case KeyEvent.VK_J:
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
                initObjects();
                render();
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

    /**
     * Updates the model matrix of the given solid according to the specified
     * action.
     *
     * If the action is ROTATION, the provided rotation matrix is
     * left-multiplied with the current model matrix.
     *
     * If the action is PROOFING, the provided translation matrix is
     * right-multiplied with the current model matrix.
     *
     * Transformations are appended on the right side of the model matrix.
     *
     * @param solid            the solid whose model matrix will be updated
     * @param action           the transformation type (ROTATION or PROOFING)
     * @param translationValue translation matrix (required for PROOFING)
     * @param rotationValue    rotation matrix (required for ROTATION)
     */
    private void updateSolid(Solid solid, SolidAction action, Mat4Transl translationValue,
            Mat4 rotationValue) {

        if (solid == null || action == null) {
            System.out.println("Solid and action are required");
            return;
        }

        switch (action) {
            case ROTATION:
                if (rotationValue == null) {
                    System.out.println("Rotation matrix is required.");
                    return;
                }

                solid.setModel(rotationValue.mul(solid.getModel()));
                break;
            case PROOFING:
                if (translationValue == null) {
                    System.out.println("Translation matrix is required.");
                    return;
                }

                solid.setModel(solid.getModel().mul(translationValue));
                break;
            default:
                break;
        }
    }

    private void render() {
        panel.clear();
        zBuffer.clear();

        colorDrawMode = panel.getColorDrawMode();
        colorFillMode = panel.getColorFillMode();

        renderer.setView(camera.getViewMatrix());
        renderer.setProj(projectionMatrix);

        for (int i = 0; i < scene.getSolids().size(); i++) {
            Solid solid = scene.getSolids().get(i);

            if (i == activeSolidIndex) {
                solid.setState(SolidState.SELECTED);

            } else
                solid.setState(SolidState.NORMAL);

            setRasterizerFillColor(renderer, solid);
            setRasterizerDrawColor(renderer, solid);

            renderer.render(solid);
        }

        update();
    }

    private void setRasterizerDrawColor(Renderer renderer, Solid solid) {
        if (solidModel != SolidModel.WIREFRAME)
            return;

        if (solid.getState() == SolidState.SELECTED) {
            if (colorDrawMode == ColorDrawMode.GRADIENT) {
                solid.setShader(shaderInterpolated);
            } else if (colorDrawMode == ColorDrawMode.SOLID)
                solid.setShader(shaderConstant);
            else {
                System.err.println("Invalid ColorMode, falling back to SOLID");
                solid.setShader(shaderConstant);
            }
        } else {
            if (solid.getShader() == null) {
                solid.setShader(shaderConstant);
            }

            solid.setShader(solid.getShader());
        }
    }

    private void setRasterizerFillColor(Renderer renderer, Solid solid) {
        if (solidModel != SolidModel.SOLID)
            return;

        if (solid.getState() == SolidState.SELECTED) {
            if (colorFillMode == ColorFillMode.GRADIENT)
                solid.setShader(shaderInterpolated);
            else if (colorFillMode == ColorFillMode.CONSTANT) {
                if (solid.getUsePongShader()) {
                    solid.setShader(phongShader);
                } else {
                    solid.setShader(shaderConstant);
                }
            } else if (colorFillMode == ColorFillMode.TEXTURE)
                if (solid.getTexture() == null) {
                    solid.setShader(shaderConstant);
                } else {
                    solid.setShader(new ShaderTexture(solid.getTexture()));
                }
            else {
                System.err.println("Invalid ColorMode, falling back to SOLID");
                solid.setShader(shaderConstant);
            }
        } else {
            if (solid.getShader() == null) {
                solid.setShader(shaderConstant);
            }

            solid.setShader(solid.getShader());
        }
    }

    /**
     * Repaints the panel.
     */
    private void update() {
        panel.repaint();
    }

    private Vec3D getCenterVec(List<Vertex> vertexes) {
        double x = 0;
        double y = 0;
        double z = 0;

        for (Vertex v : vertexes) {
            x += v.getX();
            y += v.getY();
            z += v.getZ();
        }

        return new Vec3D(x / vertexes.size(), y / vertexes.size(), z / vertexes.size());
    }
}
