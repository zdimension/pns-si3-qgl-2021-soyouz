package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Polygon;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Rectangle;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.*;
import fr.unice.polytech.si3.qgl.soyouz.tooling.model.SimulatorModel;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;

import java.util.*;
import java.util.stream.IntStream;

public class SimulatorCanvas3D extends JFXPanel implements SimulatorView
{
    public static final PhongMaterial MAT_VIEW = new PhongMaterial(Color.rgb(32, 32, 32, 0.3));
    private static final Color BACKGROUND = Color.rgb(202, 219, 255);
    private static final Map<Class<? extends ShapedEntity>, Color> ENTITY_COLORS = Map.of(
        Bateau.class, Color.rgb(193, 169, 134),
        AutreBateau.class, Color.rgb(84, 72, 28),
        Stream.class, Color.rgb(36, 36, 203, 0.5),
        Reef.class, Color.rgb(12, 191, 12)
    );
    private static final double CAMERA_INITIAL_DISTANCE = -450;
    private static final double CAMERA_INITIAL_X_ANGLE = 0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 0;
    private static final double CAMERA_INITIAL_Z_ANGLE = 0;
    private static final double CAMERA_NEAR_CLIP = 10;
    private static final double CAMERA_FAR_CLIP = 100000.0;
    private static final double AXIS_LENGTH = 2000.0;
    private static final double HYDROGEN_ANGLE = 104.5;
    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 30;
    private static final double SCALE_WHEEL_FACTOR = 0.1;
    public static final int AXIS_THICKNESS = 10;
    final Group root = new Group();
    final Xform axisGroup = new Xform();
    final Xform moleculeGroup = new Xform();
    final Xform world = new Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    final Xform entities = new Xform();
    private final SimulatorModel model;
    private final Simulator simulator;
    private final double DEFAULT_HEIGHT = 100;
    private final PhongMaterial MAT_BLACK = new PhongMaterial(Color.BLACK);
    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;
    private boolean centered = false;
    private LinkedList<Position>[] shipHistory;
    private boolean drawPath = true;
    private boolean drawNodes = true;
    private boolean debugCollisions;

    public SimulatorCanvas3D(SimulatorModel model,
                             Simulator simulator)
    {
        this.model = model;
        this.simulator = simulator;

        var sea = new Box(1e6, 1e6, 1000);
        sea.setTranslateZ(500);
        var mat = new PhongMaterial(Color.rgb(23, 23, 223, 0.6));
        sea.setMaterial(mat);
        root.getChildren().add(world);
        root.getChildren().add(entities);
        root.getChildren().add(sea);
        root.setDepthTest(DepthTest.ENABLE);

        buildCamera();
        buildAxes();

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

        setScene(scene);

        scene.setCamera(camera);
    }

    private void repaintEx()
    {
        this.setSize(this.getWidth(), this.getHeight() - 1);
        this.setSize(this.getWidth(), this.getHeight() + 1);
    }

    public void onDrag(MouseEvent me)
    {
        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
        mousePosX = me.getSceneX();
        mousePosY = me.getSceneY();
        mouseDeltaX = (mousePosX - mouseOldX);
        mouseDeltaY = (mousePosY - mouseOldY);

        double modifier = 1.0;

        if (me.isPrimaryButtonDown())
        {
            cameraXform.rz.setAngle(cameraXform.rz.getAngle() + mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);
            cameraXform.rx.setAngle(cameraXform.rx.getAngle() - mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED);
        }
        else if (me.isSecondaryButtonDown())
        {
            cameraXform2.t.setX(cameraXform2.t.getX() - mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
            cameraXform2.t.setY(cameraXform2.t.getY() - mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
        }
        repaintEx();
    }

    private void buildCamera()
    {
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
        cameraXform.rz.setAngle(CAMERA_INITIAL_Z_ANGLE);
        camera.setTranslateZ(-14000);
        cameraXform.t.setX(3000);
        cameraXform.t.setY(5000);
    }

    private void buildAxes()
    {
        System.out.println("buildAxes()");
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
        debugCollisions = selected;
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
        if (getWidth() == 0) return; // initialization

        var min = getEntitiesPositions().reduce(Point2d::min).get();
        var max = getEntitiesPositions().reduce(Point2d::max).get();
        var diff = max.sub(min);

        var pos = diff.mul(0.5).add(min);
        cameraXform.t.setX(pos.x);
        cameraXform.t.setY(pos.y);

        System.out.println(camera.getFieldOfView());
        camera.setTranslateZ(-Math.max(diff.x, diff.y) / (Math.tan(Math.toRadians(camera.getFieldOfView()))));

        //scale = Math.min(getWidth() / diff.x, getHeight() / diff.y) * 0.8;

        centered = true;

        repaintEx();
    }

    @Override
    public void reset()
    {
        clearHistory();
        update();
    }

    @Override
    public void update()
    {
        Platform.runLater(() ->
        {
            entities.getChildren().clear();
            drawGame();
            repaintEx();
        });
    }

    @Override
    public SimulatorModel getModel()
    {
        return model;
    }

    private void drawGame()
    {
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

        drawEntity(model.getShip());

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

        drawShipVision(model.getShip());
    }

    private static final List<Class<? extends ShapedEntity>> ENTITY_DRAW_ORDER = List.of(
        Bateau.class,
        ShapedEntity.class,
        Stream.class
    );

    private void drawNodes()
    {
        var cp = model.cockpits[0].getCurrentCheckpoint();
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
                drawLine(line.first.position, line.second.position, Color.ORANGE);
            }
        }

        var path = cp.path;
        for (int i = 0; i < path.size() - 1; i++)
        {
            drawLine(path.get(i).position, path.get(i + 1).position, Color.MAGENTA);
        }

        if (drawNodes)
        {
            for (Point2d p : cp.nodes)
            {
                drawShape(new Circle(10), p.toPosition(), Color.BLACK, true);
            }
        }
    }

    private void drawLine(Point2d a, Point2d b, Color c)
    {
        var diff = b.sub(a);
        var mid = b.mid(a);
        var shp = new Box(diff.norm(), 10, 10);
        shp.setMaterial(new PhongMaterial(c));
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
                           Position p, Color color, boolean useSphere)
    {
        var mat = new PhongMaterial(color);

        Shape3D shape;
        if (s instanceof Circle)
        {
            var rad = ((Circle) s).getRadius();
            shape = useSphere
                ? new Sphere(rad)
                : new Cylinder(rad, DEFAULT_HEIGHT);
        }
        else if (s instanceof Rectangle)
        {
            var r = (Rectangle) s;
            shape = new Box(r.getHeight(), r.getWidth(), DEFAULT_HEIGHT);
        }
        else if (s instanceof fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Polygon)
        {
            var msh = new TriangleMesh();
            var poly = (Polygon)s;
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
            mpts.addAll((float)ctr.x, (float)ctr.y, (float)(DEFAULT_HEIGHT / 2));
            mpts.addAll((float)ctr.x, (float)ctr.y, (float)(-DEFAULT_HEIGHT / 2));
            var j = pts.length - 1;
            for (int i = 0; i < pts.length; j = i++)
            {
                j = (i + 1) % pts.length;
                fcs.addAll(i, 0, j, 0, ctop, 0);
                fcs.addAll(ctop, 0, j, 0, i, 0);
                fcs.addAll(i, 0, j, 0, cbot, 0);
                fcs.addAll(cbot, 0, j, 0, i, 0);
            }

            shape = new MeshView(msh);
        }
        else
        {
            return;
        }

        if (shape instanceof Cylinder)
        {
            shape.setRotationAxis(Rotate.X_AXIS);
            shape.setRotate(90);
        }
        else
        {
            shape.getTransforms().add(new Rotate(Math.toDegrees(p.getOrientation()), Rotate.Z_AXIS));
        }
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
                drawLine(prev, elem, Color.BLACK);
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
        drawShape(c.getShape(), c.getPosition(), Color.RED, true);
    }
}
