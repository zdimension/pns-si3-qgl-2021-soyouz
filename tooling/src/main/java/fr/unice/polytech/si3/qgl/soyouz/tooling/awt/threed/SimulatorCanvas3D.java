package fr.unice.polytech.si3.qgl.soyouz.tooling.awt.threed;

import com.interactivemesh.jfx.importer.tds.TdsModelImporter;
import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Polygon;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Rectangle;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.*;
import fr.unice.polytech.si3.qgl.soyouz.tooling.awt.SimulatorView;
import fr.unice.polytech.si3.qgl.soyouz.tooling.awt.threed.cloth.ClothMesh;
import fr.unice.polytech.si3.qgl.soyouz.tooling.model.SimulatorModel;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.*;
import java.util.stream.IntStream;

public class SimulatorCanvas3D extends JFXPanel implements SimulatorView
{
    public static final PhongMaterial MAT_VIEW = new PhongMaterial(Color.rgb(32, 32, 32, 0.3));
    public static final int AXIS_THICKNESS = 10;
    private static final Color BACKGROUND = Color.rgb(202, 219, 255);
    private static final Map<Class<? extends ShapedEntity>, PhongMaterial> ENTITY_COLORS = Map.of(
        Bateau.class, new PhongMaterial(Color.rgb(193, 169, 134)),
        AutreBateau.class, new PhongMaterial(Color.rgb(84, 72, 28)),
        Stream.class, new PhongMaterial(Color.rgb(36, 36, 203, 0.5)),
        Reef.class, new PhongMaterial(Color.rgb(12, 191, 12))
    );
    private static final double CAMERA_NEAR_CLIP = 10;
    private static final double CAMERA_FAR_CLIP = 300000.0;
    private static final double AXIS_LENGTH = 2000.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 30;
    private static final double SCALE_WHEEL_FACTOR = 0.1;
    private static final List<Class<? extends ShapedEntity>> ENTITY_DRAW_ORDER = List.of(
        Bateau.class,
        ShapedEntity.class,
        Stream.class
    );
    private static final PhongMaterial MAT_CHECKPOINT = new PhongMaterial(Color.rgb(255, 0, 0,
        0.7));
    final Group root = new Group();
    final Xform axisGroup = new Xform();
    final Xform world = new Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform entities = new Xform();
    private final Rotate clothRotate = new Rotate(0, Rotate.Y_AXIS);
    private final SimulatorModel model;
    private final double DEFAULT_HEIGHT = 100;
    private final PhongMaterial MAT_BLACK = new PhongMaterial(Color.BLACK);
    private final PhongMaterial MAT_MAGENTA = new PhongMaterial(Color.MAGENTA);
    private final PhongMaterial MAT_ORANGE = new PhongMaterial(Color.ORANGE);
    private final Map<Shape, Shape3D> shapeCache = new HashMap<>();
    private final Group shipShape;
    private final Rotate shipRotZ;
    private final Rotate shipRotTransverse;
    private final Translate lightPos;
    public boolean fpsView;
    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;
    private Group shipMesh;
    private boolean centered = false;
    private LinkedList<Position>[] shipHistory;
    private boolean drawPath = true;
    private boolean drawNodes = true;
    private double lastOrientation;
    public SimulatorCanvas3D(SimulatorModel model)
    {
        this.model = model;

        initShip();

        var sea = new Box(1e6, 1e6, 0);
        sea.setTranslateZ(0);
        var mat = new PhongMaterial(Color.rgb(23, 23, 223, 0.6));
        sea.setMaterial(mat);
        buildAxes();

        root.getChildren().add(world);
        world.getChildren().add(entities);
        world.getChildren().add(sea);
        world.getChildren().add(new AmbientLight(Color.rgb(80, 80, 80, 1)));
        var lit = new PointLight(Color.rgb(255, 255, 255, 1));
        lit.getTransforms().add(lightPos = new Translate());
        lit.setTranslateZ(-5000);
        lit.setConstantAttenuation(1);
        lit.setQuadraticAttenuation(3e-8);
        world.getChildren().add(lit);
        root.setDepthTest(DepthTest.ENABLE);

        buildCamera();

        Scene scene = new Scene(root, 500, 300, true, SceneAntialiasing.BALANCED);
        scene.setFill(BACKGROUND);
        scene.setOnMousePressed(me ->
        {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
            repaintEx();
        });
        scene.setOnMouseMoved(e -> this.repaintEx());
        scene.setOnMouseDragged(this::onDrag);
        scene.setOnScroll(e ->
        {
            var factor = 1 + SCALE_WHEEL_FACTOR;
            if (e.getDeltaY() > 0)
            {
                factor = 1 / factor;
            }
            camera.setTranslateZ(camera.getTranslateZ() * factor);
            repaintEx();
        });
        scene.setOnKeyPressed(e ->
        {
            if (e.getCode() == KeyCode.F)
            {
                setFpsView(!fpsView);
            }
        });

        setScene(scene);

        scene.setCamera(camera);
        shipShape = new Group(shipMesh);
        shipRotZ = new Rotate(0, Rotate.Z_AXIS);
        shipRotTransverse = new Rotate(0, Rotate.Y_AXIS);
        shipShape.getTransforms().addAll(shipRotZ, shipRotTransverse);
    }

    void initShip()
    {
        var x = new TdsModelImporter();
        x.setResourceBaseUrl(SimulatorCanvas3D.class.getResource("ship/"));
        x.read(SimulatorCanvas3D.class.getResource("ship/ST_MARIA.3DS"));
        var msh = x.getImport();
        var mats = x.getNamedMaterials();
        var gray = Color.rgb(199, 199, 199, 0.85);
        mats.get("LEON").setDiffuseColor(gray);
        mats.get("LARGE").setDiffuseColor(gray);
        mats.get("GLASS").setDiffuseColor(Color.rgb(13, 50, 1, 0.5));
        mats.get("BLUE").setDiffuseColor(gray);
        shipMesh = new Group(msh);
        shipMesh.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
        shipMesh.setScaleX(0.1);
        shipMesh.setScaleY(0.1);
        shipMesh.setScaleZ(0.1);
        shipMesh.setTranslateX(145.5);
        shipMesh.setTranslateY(1900);
        shipMesh.setTranslateZ(260);
        var light = new PointLight(Color.ORANGE);
        light.setMaxRange(1000);
        light.setConstantAttenuation(0.1);
        light.setLinearAttenuation(0.1);
        light.getTransforms().add(new Translate(-180, -1090, 2010));
        shipMesh.getChildren().addAll(light);

        ClothMesh cloth = new ClothMesh(1200.0, 720.0);
        cloth.setPerPointMass(10);
        cloth.setBendStrength(0.5);
        cloth.setStretchStrength(1);
        cloth.setShearStrength(0.5);
        cloth.setDrawMode(DrawMode.FILL);
        cloth.setCullFace(CullFace.NONE);
        cloth.setDiffuseMap(new Image("/fr/unice/polytech/si3/qgl/soyouz/tooling/awt/threed/ship" +
            "/flag.png"));
        cloth.setSpecularPower(5);
        cloth.getTransforms().addAll(
            new Rotate(-90, Rotate.Y_AXIS),
            new Translate(110, -4080, 160),
            new Translate(-600, 0),
            clothRotate,
            new Translate(600, 0));
        cloth.startSimulation();

        shipMesh.getChildren().add(cloth);

        x.close();
    }

    private void repaintEx()
    {

    }

    public void onDrag(MouseEvent me)
    {
        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
        mousePosX = me.getSceneX();
        mousePosY = me.getSceneY();
        mouseDeltaX = (mousePosX - mouseOldX);
        mouseDeltaY = (mousePosY - mouseOldY);

        if (me.isPrimaryButtonDown())
        {
            cameraXform.setRx(cameraXform.rx.getAngle() - mouseDeltaY * MOUSE_SPEED * ROTATION_SPEED);
            cameraXform.setRz(cameraXform.rz.getAngle() + mouseDeltaX * MOUSE_SPEED * ROTATION_SPEED);
        }
        else if (me.isSecondaryButtonDown())
        {
            var angle = Math.toRadians(cameraXform.rz.getAngle());
            var p2d = Point2d.fromPolar(mouseDeltaX, angle)
                .add(Point2d.fromPolar(mouseDeltaY, angle).ortho().mul(1 / Math.cos(Math.toRadians(-cameraXform.rx.getAngle()))))
                .mul(MOUSE_SPEED * TRACK_SPEED
                    * (camera.getTranslateZ() / -5000));
            cameraXform.setTranslate(
                cameraXform.t.getX() - p2d.getX(),
                cameraXform.t.getY() - p2d.getY()
            );
        }
        repaintEx();
    }

    private void buildCamera()
    {
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(camera);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
    }

    private void buildAxes()
    {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Box xAxis = new Box(AXIS_LENGTH, AXIS_THICKNESS, AXIS_THICKNESS);
        final Box yAxis = new Box(AXIS_THICKNESS, AXIS_LENGTH, AXIS_THICKNESS);
        final Box zAxis = new Box(AXIS_THICKNESS, AXIS_THICKNESS, AXIS_LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        axisGroup.setVisible(true);
        world.getChildren().addAll(axisGroup);
    }

    @Override
    public void clearHistory()
    {
        shipHistory = new LinkedList[model.getShips().length];
        for (int i = 0; i < shipHistory.length; i++)
        {
            shipHistory[i] = new LinkedList<>();
        }
        update();
    }

    @Override
    public void setDrawPath(boolean selected)
    {
        drawPath = selected;
        update();
    }

    @Override
    public void setDrawNodes(boolean selected)
    {
        drawNodes = selected;
        update();
    }

    @Override
    public void setDebugCollisions(boolean selected)
    {
        update();
    }

    private java.util.stream.Stream<Point2d> getEntitiesPositions()
    {
        var str = java.util.stream.Stream.concat(getVisibleShapes().stream(),
            java.util.stream.Stream.of(model.getShip()));
        var goal = model.getGoal();
        if (goal instanceof RegattaGoal)
        {
            str = java.util.stream.Stream.concat(str,
                Arrays.stream(((RegattaGoal) goal).getCheckpoints()));
        }
        return str.map(ShapedEntity::getPosition);
    }

    @Override
    public void centerView(boolean force)
    {
        if (centered && !force) return;

        fpsView = false;

        var min = getEntitiesPositions().reduce(Point2d::min).get();
        var max = getEntitiesPositions().reduce(Point2d::max).get();
        var diff = max.sub(min);

        var pos = diff.mul(0.5).add(min);
        cameraXform.setRotate(0, 0, 0);
        cameraXform.setTranslate(pos.x, pos.y);
        lightPos.setX(pos.x);
        lightPos.setY(pos.y);

        camera.setTranslateZ(-Math.max(diff.x, diff.y) / (Math.tan(Math.toRadians(camera.getFieldOfView()))));

        centered = true;

        repaintEx();
    }

    @Override
    public void reset()
    {
        shapeCache.clear();
        shipRotTransverse.setAngle(0);
        clearHistory();
        setFpsView(fpsView);
        update();
    }

    @Override
    public void update()
    {
        Platform.runLater(() ->
        {
            clothRotate.setAngle(180 + Math.toDegrees(model.getWind().getOrientation() - model.getShip().getPosition().getOrientation()));
            System.out.println(clothRotate.getAngle());
            entities.getChildren().clear();
            drawGame();
        });
    }

    @Override
    public SimulatorModel getModel()
    {
        return model;
    }

    private void drawGame()
    {
        Bateau ship = model.getShip();
        var p = ship.getPosition();
        shipRotZ.setAngle(Math.toDegrees(p.getOrientation()) - 90);
        shipShape.setTranslateX(p.getX());
        shipShape.setTranslateY(p.getY());
        entities.getChildren().add(shipShape);

        drawShipHistory();

        if (model.nps[0] != null)
        {
            getVisibleShapes()
                .stream().sorted(Comparator.comparingInt(
                obj -> IntStream.range(0, ENTITY_DRAW_ORDER.size())
                    .filter(i -> ENTITY_DRAW_ORDER.get(i).isInstance(obj))
                    .findFirst().getAsInt()
            )).forEach(this::drawEntity);
        }

        drawNodes();

        SimulatorView.updateHistory(model, shipHistory);

        var goal = model.getGoal();
        if (goal instanceof RegattaGoal)
        {
            var rg = (RegattaGoal) goal;
            Checkpoint[] checkpoints = rg.getCheckpoints();
            for (int i = 0; i < checkpoints.length; i++)
            {
                drawCheckpoint(checkpoints[i], i);
            }
        }

        //drawShipVision(ship);

        if (fpsView)
        {
            var sp = ((Point2d) p).add(Point2d.fromPolar(-160, p.getOrientation()));
            cameraXform.setTranslate(sp.x, sp.y,
                -DEFAULT_HEIGHT / 2 - 90);
        }

        if (!Double.isNaN(lastOrientation))
        {
            var diff = p.getOrientation() - lastOrientation;
            if (fpsView && diff != 0)
            {
                cameraXform.setRz(cameraXform.rz.getAngle() + Math.toDegrees(diff));
            }
            if (Math.abs(diff) > 1e-5)
            {
                shipRotTransverse.setAngle(shipRotTransverse.getAngle() + diff * 100);
            }
        }

        var clamped = shipRotTransverse.getAngle() * 0.9;
        if (clamped > 30)
        {
            clamped = 30;
        }
        if (clamped < -30)
        {
            clamped = -30;
        }
        shipRotTransverse.setAngle(clamped);

        lastOrientation = p.getOrientation();
    }

    public void setFpsView(boolean enable)
    {
        if (enable)
        {
            lastOrientation = Double.NaN;
            camera.setTranslateZ(-100);
            cameraXform.setRotate(-90, 180,
                -90 + Math.toDegrees(model.getShip().getPosition().getOrientation()));
        }
        else
        {
            if (fpsView)
            {
                centerView(true);
            }
        }
        fpsView = enable;
        update();
    }

    private void drawNodes()
    {
        if (model.cockpits[0] instanceof Cockpit)
        {
            var cockpit = (Cockpit) model.cockpits[0];

            var cp = cockpit.getCurrentCheckpoint();
            if (cp == null)
            {
                return;
            }
            var graph = cp.graph;
            if (graph == null)
            {
                return;
            }

            if (drawPath)
            {
                for (var line : graph.getEdges())
                {
                    drawLine(line.first.position, line.second.position, MAT_ORANGE, 11);
                }
            }

            var path = cp.path;
            for (int i = 0; i < path.size() - 1; i++)
            {
                drawLine(path.get(i).position, path.get(i + 1).position, MAT_MAGENTA, 12);
            }

            if (drawNodes)
            {
                ArrayList<Point2d> copy;
                synchronized (cp.nodes)
                {
                    copy = new ArrayList<>(cp.nodes);
                }
                for (Point2d p : copy)
                {
                    drawShape(new Circle(10), p.toPosition(), MAT_BLACK, true);
                }
            }
        }
    }

    private void drawLine(Point2d a, Point2d b, PhongMaterial m, double thickness)
    {
        var diff = b.sub(a);
        var mid = b.mid(a);
        var shp = new Box(diff.norm(), thickness, thickness);
        shp.setMaterial(m);
        shp.setTranslateX(mid.x);
        shp.setTranslateY(mid.y);
        shp.setRotationAxis(Rotate.Z_AXIS);
        shp.setRotate(Math.toDegrees(diff.angle()));
        entities.getChildren().add(shp);
    }

    private void drawEntity(ShapedEntity se)
    {
        drawShape(se.getShape(), se.getPosition(),
            ENTITY_COLORS.get(se.getClass()), false);
    }

    private void drawShape(Shape s,
                           Position p, PhongMaterial mat, boolean useSphere)
    {
        Shape3D shape = shapeCache.getOrDefault(s, null);
        if (shape == null)
        {
            if (s instanceof Circle)
            {
                var rad = ((Circle) s).getRadius();
                if (useSphere)
                {
                    shape = new Sphere(rad);
                }
                else
                {
                    shape = new Cylinder(rad, DEFAULT_HEIGHT);
                    shape.setRotationAxis(Rotate.X_AXIS);
                    shape.setRotate(90);
                }
            }
            else
            {
                if (s instanceof Rectangle)
                {
                    var r = (Rectangle) s;
                    shape = new Box(r.getHeight(), r.getWidth(), DEFAULT_HEIGHT);
                }
                else if (s instanceof Polygon)
                {
                    var msh = new TriangleMesh();
                    var poly = (Polygon) s;
                    var pts = poly.getVertices();
                    var ctr = poly.getCenter();
                    var mpts = msh.getPoints();
                    var fcs = msh.getFaces();
                    float[] var7 = new float[2 * pts.length + 4];
                    msh.getTexCoords().addAll(var7);
                    var ctop = pts.length;
                    var cbot = ctop + 1;
                    for (Point2d pt : pts)
                    {
                        mpts.addAll((float) pt.x, (float) pt.y, 0);
                    }
                    mpts.addAll((float) ctr.x, (float) ctr.y, (float) (DEFAULT_HEIGHT * 2));
                    mpts.addAll((float) ctr.x, (float) ctr.y, (float) (-DEFAULT_HEIGHT / 2));
                    var j = pts.length - 1;
                    for (int i = 0; i < pts.length; j = i++)
                    {
                        fcs.addAll(j, 0, i, 0, ctop, 0);
                        fcs.addAll(ctop, 0, i, 0, j, 0);
                        fcs.addAll(j, 0, i, 0, cbot, 0);
                        fcs.addAll(cbot, 0, i, 0, j, 0);
                    }

                    shape = new MeshView(msh);
                }
                else
                {
                    return;
                }

                shape.getTransforms().clear();
                shape.getTransforms().add(new Rotate(Math.toDegrees(p.getOrientation()),
                    Rotate.Z_AXIS));
            }
        }

        shapeCache.put(s, shape);

        shape.setTranslateX(p.getX());
        shape.setTranslateY(p.getY());
        shape.setMaterial(mat);
        entities.getChildren().add(shape);
    }

    private void drawShipHistory()
    {
        for (var history : shipHistory)
        {
            if (history.size() < 2)
            {
                continue;
            }
            var iter = history.iterator();
            var prev = iter.next();
            while (iter.hasNext())
            {
                var elem = iter.next();
                drawLine(prev, elem, MAT_BLACK, 10);
                prev = elem;
            }
        }
    }

    private void drawShipVision(Bateau b)
    {
        var cyl = new Cylinder(1000, DEFAULT_HEIGHT * 1.1);
        cyl.setMaterial(MAT_VIEW);
        var pos = b.getPosition();
        cyl.setRotationAxis(Rotate.X_AXIS);
        cyl.setRotate(90);
        cyl.setTranslateX(pos.x);
        cyl.setTranslateY(pos.y);
        entities.getChildren().add(cyl);
    }

    private void drawCheckpoint(Checkpoint c, int i)
    {
        drawShape(c.getShape(), c.getPosition(), MAT_CHECKPOINT, true);
    }
}
