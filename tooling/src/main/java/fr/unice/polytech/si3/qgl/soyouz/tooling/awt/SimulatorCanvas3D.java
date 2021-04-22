package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Rectangle;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.*;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Voile;
import fr.unice.polytech.si3.qgl.soyouz.classes.pathfinding.Node;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;
import fr.unice.polytech.si3.qgl.soyouz.tooling.model.SimulatorModel;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.time.Duration;
import java.util.*;

public class SimulatorCanvas3D extends JFXPanel implements SimulatorView
{
    private static final Color BACKGROUND = Color.rgb(202, 219, 255);
    private static final Map<Class<? extends ShapedEntity>, Color> ENTITY_COLORS = Map.of(
        Bateau.class, Color.rgb(193, 169, 134),
        AutreBateau.class, Color.rgb(84, 72, 28),
        Stream.class, Color.rgb(36, 36, 203),
        Reef.class, Color.rgb(12, 191, 12)
    );
    private static final double CAMERA_INITIAL_DISTANCE = -450;
    private static final double CAMERA_INITIAL_X_ANGLE = 0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 0;
    private static final double CAMERA_INITIAL_Z_ANGLE = 0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 100000.0;
    private static final double AXIS_LENGTH = 2000.0;
    private static final double HYDROGEN_ANGLE = 104.5;
    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 0.3;
    final Group root = new Group();
    final Xform axisGroup = new Xform();
    final Xform moleculeGroup = new Xform();
    final Xform world = new Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    private final SimulatorModel model;
    private final Simulator simulator;
    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;
    final Xform entities = new Xform();


    private static final double SCALE_WHEEL_FACTOR = 0.1;
    public SimulatorCanvas3D(SimulatorModel model,
                             Simulator simulator)
    {
        this.model = model;
        this.simulator = simulator;

        root.getChildren().add(world);
        root.getChildren().add(entities);
        root.setDepthTest(DepthTest.ENABLE);

        buildCamera();
        buildAxes();

        Scene scene = new Scene(root, 500, 300, true);
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
            repaint();
        });

        setScene(scene);

        scene.setCamera(camera);
    }

    private void repaintEx()
    {
        this.setSize(this.getWidth() - 1, this.getHeight());
        this.setSize(this.getWidth() + 1, this.getHeight());
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
            double z = camera.getTranslateZ();
            double newZ = z + mouseDeltaX * MOUSE_SPEED * modifier;
            camera.setTranslateZ(newZ);
        }
        else if (me.isMiddleButtonDown())
        {
            cameraXform2.t.setX(cameraXform2.t.getX() - mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
            cameraXform2.t.setY(cameraXform2.t.getY() - mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
        }
        System.out.println(camera.getTranslateX() + ";" + camera.getTranslateY() + ";" + camera.getTranslateZ() + ";" + cameraXform.rx.getAngle()+ ";" + cameraXform.ry.getAngle()+ ";" + cameraXform.rz.getAngle()+ ";" + cameraXform.t.getX()+ ";" + cameraXform.t.getY());
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
        //camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
        cameraXform.rz.setAngle(CAMERA_INITIAL_Z_ANGLE);
        /*camera.setTranslateX(3000);
        camera.setTranslateY(-1000);*/
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

        final Box xAxis = new Box(AXIS_LENGTH, 100, 100);
        final Box yAxis = new Box(100, AXIS_LENGTH, 100);
        final Box zAxis = new Box(100, 100, AXIS_LENGTH);

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
        repaint();
    }

    @Override
    public void setDrawPath(boolean selected)
    {
        drawPath = selected;
        repaint();
    }

    @Override
    public void setDrawNodes(boolean selected)
    {
        drawNodes = selected;
        repaint();
    }

    @Override
    public void setDebugCollisions(boolean selected)
    {
        debugCollisions = selected;
        repaint();
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

    private boolean centered = false;

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

        //scale = Math.min(getWidth() / diff.x, getHeight() / diff.y) * 0.8;

        centered = true;

        repaint();
    }

    @Override
    public void reset()
    {
        clearHistory();
        repaint();
    }

    @Override
    public void repaint()
    {
        Platform.runLater(() ->
        {
            entities.getChildren().clear();
            
            drawGame();
            
            /*

            for (ShapedEntity visibleShape : getVisibleShapes())
            {
                var shp = visibleShape.getShape();
                if (shp instanceof Rectangle)
                {
                    var r = (Rectangle)shp;
                    var box = new Box(r.getWidth(), 10, r.getHeight());
                    box.setTranslateX(visibleShape.getPosition().x);
                    box.setTranslateY(visibleShape.getPosition().y);
                    entities.getChildren().add(box);
                }
            }*/
        });

        super.repaint();
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

        if (model.nps[0] != null)
        {
            getVisibleShapes()
                .stream().sorted(Comparator.comparingInt(
                obj -> obj instanceof Bateau ? 1 : -1
            )).forEach(ent -> drawEntity(ent));
        }

        drawEntity(model.getShip());

        drawShipVision(model.getShip());

        drawNodes();

        drawShipHistory();

        var ships = model.getShips();
        for (int i = 0; i < ships.length; i++)
        {
            var history = shipHistory[i];
            var shipPos = ships[i].getPosition();
            if (history.isEmpty() || !shipPos.equals(history.getLast()))
            {
                history.add(shipPos);
            }
        }

        drawShipDeck(model.getShip(), model.getSailors());

        drawLegendText();
    }

    private void drawNodes()
    {
        /*var cp = model.cockpits[0].getCurrentCheckpoint();
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
            g.setColor(java.awt.Color.ORANGE);

            for (Pair<Node, Node> line : graph.getEdges())
            {
                drawLine(g,
                    mapToScreen(line.first.position),
                    mapToScreen(line.second.position));
            }
        }

        g.setColor(java.awt.Color.MAGENTA);
        var path = cp.path;
        for (int i = 0; i < path.size() - 1; i++)
        {
            drawLine(g,
                mapToScreen(path.get(i).position),
                mapToScreen(path.get(i + 1).position));
        }

        if (drawNodes)
        {
            g.setColor(java.awt.Color.BLACK);
            for (Point2d p : cp.nodes)
            {
                drawShape(new Circle(mapToWorld(3)), p.toPosition(), false);
            }
        }*/
    }

    private LinkedList<Position>[] shipHistory;
    private boolean drawPath = true;
    private boolean drawNodes = true;
    private boolean debugCollisions;

    private void drawLine(Point a, Point b)
    {
        //g.drawLine(a.x, a.y, b.x, b.y);
    }

    private void drawLegendText()
    {
        /*g = (Graphics2D) g.create();

        g.setColor(java.awt.Color.BLACK);

        var mouse = this.getMousePosition();
        if (mouse != null)
        {
            var p = mapToWorld(mouse);
            g.drawString(String.format("X = %6.2f", p.x), 20, getHeight() - 40);
            g.drawString(String.format("Y = %6.2f", p.y), 20, getHeight() - 20);

            if (debugCollisions)
            {
                var sp = model.getShip().getPosition();
                for (ShapedEntity shp : getVisibleShapes())
                {
                    if (shp.getShape().linePassesThrough(shp.toLocal(p), shp.toLocal(sp), 0))
                    {
                        g.setColor(java.awt.Color.MAGENTA);
                        drawShape(shp.getShape(), shp.getPosition(), false);
                    }
                }
                g.setColor(java.awt.Color.BLACK);
                drawLine(mouse, mapToScreen(sp));
            }
        }

        g.setColor(java.awt.Color.BLACK);

        if (simulator.timer.isRunning())
        {
            g.drawString(fps + " FPS", 20, 20);
        }

        if (model.getWind() != null && model.getWind().getStrength() != 0)
        {
            g.setStroke(HISTORY);
            g.translate(getWidth() - 40, getHeight() - 55);

            g.drawString("Wind=" + model.getWind().getStrength(), -28, 45);
            g.scale(1, 1);
            g.fillOval(-3, -3, 7, 7);
            g.rotate(model.getWind().getOrientation() - Math.PI / 2);
            g.drawPolygon(new Polygon(new int[] { 0, -7, 7 }, new int[] { 28, 18, 18 }, 3));
            g.drawPolygon(new Polygon(new int[] { -1, 1, 1, 7, 7, 0, -7, -7, -1, -1 },
                new int[] { 18, 18, -10, -16, -28, -22, -28, -16, -10, 14 }, 10));
        }*/
    }

    private void drawEntity(ShapedEntity se)
    {
        /*g = (Graphics2D) g.create();

        g.setColor(ENTITY_COLORS.get(se.getClass()));*/
        drawShape(se.getShape(), se.getPosition(), !(se instanceof Reef), ENTITY_COLORS.get(se.getClass()));
    }

    private void drawShape(fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape s, Position p, boolean showRot, Color color)
    {
        var mat = new PhongMaterial(color);

        Shape3D shape = null;
        if (s instanceof Circle)
        {
            shape = new Sphere(((Circle) s).getRadius());
        }
        else if (s instanceof Rectangle)
        {
            var r = (Rectangle)s;
            shape = new Box(r.getHeight(), r.getWidth(), 350);
        }
        else if (s instanceof fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Polygon)
        {
            return;
        }

        shape.setRotationAxis(Rotate.Z_AXIS);
        shape.setRotate(Math.toDegrees(p.getOrientation()));
        shape.setTranslateX(p.getX());
        shape.setTranslateY(p.getY());
        shape.setMaterial(mat);
        entities.getChildren().add(shape);
    }

    private void drawShipHistory()
    {
        /*g = (Graphics2D) g.create();

        g.setStroke(HISTORY);
        g.setColor(java.awt.Color.BLACK);

        for (var history : shipHistory)
        {
            var x = new int[history.size()];
            var y = new int[history.size()];
            for (int i = 0; i < history.size(); i++)
            {
                var conv = mapToScreen(history.get(i));
                x[i] = conv.x;
                y[i] = conv.y;
            }

            g.drawPolyline(x, y, x.length);
        }*/
    }

    private void drawShipVision(Bateau b)
    {
        /*g = (Graphics2D) g.create();

        g.setStroke(DASHED);
        g.setColor(java.awt.Color.BLACK);

        var mp = mapToScreen(b.getPosition());
        var md = mapToScreen(1000);
        g.drawOval(mp.x - md, mp.y - md, 2 * md, 2 * md);*/
    }

    private void drawShipDeck(Bateau b, Marin[] sailors)
    {
        /*g = (Graphics2D) g.create();

        g.translate(DECK_MARGIN, DECK_MARGIN);

        g.setColor(ENTITY_COLORS.get(Bateau.class));
        g.fillRect(0, 0, b.getDeck().getWidth() * DECK_GRID_SIZE,
            b.getDeck().getLength() * DECK_GRID_SIZE);

        for (OnboardEntity entity : b.getEntities())
        {
            var index = 0;
            if (entity instanceof Voile && ((Voile) entity).isOpenned())
            {
                index = 1;
            }
            var img = ENTITY_ICONS.get(entity.getClass())[index];
            g.drawImage(imentity.getY() * DECK_GRID_SIZE, entity.getX() * DECK_GRID_SIZE,
                DECK_GRID_SIZE, DECK_GRID_SIZE,
                model.usedEntities.contains(entity) ? java.awt.Color.RED : new java.awt.Color(0, true), null);
        }
        var i = 0;
        for (Marin sailor : sailors)
        {
            g.setColor(java.awt.Color.getHSBColor((float) i / sailors.length, 1, 1));
            var oldPos = simulator.smodel.sailorPositions.getOrDefault(sailor, null);
            var x = sailor.getY() * DECK_GRID_SIZE;
            var y = sailor.getX() * DECK_GRID_SIZE;
            var sh = DECK_GRID_SIZE / 2;
            if (oldPos != null && !Objects.equals(oldPos, sailor.getPos()))
            {
                var off = i - sailors.length / 2;
                var sq = 3 * DECK_GRID_SIZE / 4;
                var gt = (Graphics2D) g.create();
                gt.translate(sq + off, sq + off);
                gt.drawLine(oldPos.getY() * DECK_GRID_SIZE, oldPos.getX() * DECK_GRID_SIZE, x, y);
            }
            g.fillOval(sh + x, sh + y, sh, sh);
            g.setColor(java.awt.Color.BLACK);
            g.drawString(sailor.getId() + "", sh + x + 3, sh + y + 15);
            i++;
        }

        drawDeckGrid(b);*/
    }

    private void drawDeckGrid(Bateau b)
    {
        /*g.setColor(java.awt.Color.BLACK);

        var w = b.getDeck().getWidth();
        var rw = w * DECK_GRID_SIZE;
        var h = b.getDeck().getLength();
        var rh = h * DECK_GRID_SIZE;
        for (var x = 0; x <= w; x++)
        {
            int rx = x * DECK_GRID_SIZE;
            g.drawLine(rx, 0, rx, rh);
        }
        for (var y = 0; y <= h; y++)
        {
            int ry = y * DECK_GRID_SIZE;
            g.drawLine(0, ry, rw, ry);
        }*/
    }

    private void drawCheckpoint(Checkpoint c, int i)
    {
        drawShape(c.getShape(), c.getPosition(), false, Color.RED);
    }


    private Collection<ShapedEntity> getVisibleShapes()
    {
        return model.cockpits[0].entityMemory.values();
    }
}
