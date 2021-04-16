package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import fr.unice.polytech.si3.qgl.soyouz.Cockpit;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Point2d;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Rectangle;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.*;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.*;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.root.regatta.CheckpointObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.NextRoundParameters;
import fr.unice.polytech.si3.qgl.soyouz.classes.pathfinding.Node;
import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Pair;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimulatorCanvas extends JPanel
{
    private static final Color BACKGROUND = new Color(202, 219, 255);
    private static final Map<Class<?>, Image[]> ENTITY_ICONS;
    private static final int DECK_GRID_SIZE = 40;
    private static final int DECK_MARGIN = 30;
    private static final double SCALE_WHEEL_FACTOR = 0.1;

    private static final Map<Class<? extends ShapedEntity>, Color> ENTITY_COLORS = Map.of(
        Bateau.class, new Color(193, 169, 134),
        AutreBateau.class, new Color(84, 72, 28),
        Stream.class, new Color(36, 36, 203),
        Reef.class, new Color(12, 191, 12)
    );

    static
    {
        ENTITY_ICONS = Map.of(
            Rame.class, new String[] { "paddle.png" },
            Gouvernail.class, new String[] { "rudder.png" },
            Voile.class, new String[] { "lifted_sail.png", "lowered_sail.png" },
            Vigie.class, new String[] { "crow_nest.png" }
        ).entrySet().stream().map(e ->
        {
            try
            {
                var list = new Image[e.getValue().length];
                for (int i = 0; i < list.length; i++)
                {
                    list[i] = ImageIO.read(SimulatorCanvas.class.getResource(e.getValue()[i]));
                }
                return Map.entry(e.getKey(), list);
            }
            catch (IOException ioException)
            {
                throw new RuntimeException(ioException);
            }
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private final ArrayList<OnboardEntity> usedEntities;
    private final Stroke DASHED = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL
        , 0, new float[] { 9 }, 0);
    private final Stroke SHAPE_CROSS = new BasicStroke(3, BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_BEVEL);
    private final Stroke HISTORY = new BasicStroke(1, BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_BEVEL);
    private final List<Position> shipHistory = new ArrayList<>();
    public boolean drawPath = true;
    private InitGameParameters model;
    private boolean centered = false;
    private NextRoundParameters np;
    private double scale = 1;
    private Point2d cameraPos = new Point2d(0, 0);
    private Point2d moveOrigin = null;
    private Cockpit cockpit;

    public SimulatorCanvas(InitGameParameters model, ArrayList<OnboardEntity> usedEntities)
    {
        this.model = model;
        this.usedEntities = usedEntities;

        addMouseWheelListener(e ->
        {
            var factor = 1 + SCALE_WHEEL_FACTOR;
            if (e.getPreciseWheelRotation() > 0)
            {
                factor = 1 / factor;
            }
            var dz = 1 / scale - 1 / (scale * factor);
            cameraPos = cameraPos.add(
                getPos(e)
                    .sub(getViewCenter())
                    .mul(dz)
            );
            scale *= factor;
            repaint();
        });

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                moveOrigin = getPos(e)
                    .sub(getViewCenter())
                    .mul(1 / scale)
                    .add(cameraPos);
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                moveOrigin = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter()
        {
            @Override
            public void mouseDragged(MouseEvent e)
            {
                if (moveOrigin != null)
                {
                    cameraPos =
                        moveOrigin.sub(((getPos(e).sub(getViewCenter()).mul(1 / scale))));
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e)
            {
                repaint();

                super.mouseMoved(e);
            }
        });
    }

    public void setModel(InitGameParameters model)
    {
        this.model = model;
    }

    public void setNp(NextRoundParameters np)
    {
        this.np = np;
        centerView();
    }

    private Collection<ShapedEntity> getVisibleShapes()
    {
        return cockpit.entityMemory.values();
    }

    private java.util.stream.Stream<Point2d> getEntitiesPositions()
    {
        return java.util.stream.Stream.concat(getVisibleShapes().stream(),
            java.util.stream.Stream.of(model.getShip()))
            .map(ShapedEntity::getPosition);
    }

    private void centerView()
    {
        if (centered) return;

        var min = getEntitiesPositions().reduce(Point2d::min).get();
        var max = getEntitiesPositions().reduce(Point2d::max).get();
        cameraPos = max.sub(min).mul(0.5).add(min);

        scale = 0.1;

        centered = true;
    }

    void reset()
    {
        np = null;
        clearHistory();
        repaint();
    }

    private Point2d getViewCenter()
    {
        return new Point2d(getWidth(), getHeight()).mul(0.5);
    }

    private Point2d getPos(MouseEvent e)
    {
        return new Point2d(e.getX(), e.getY());
    }

    /**
     * Appelle la {@link #paintBuffer(Graphics2D) fonction principale de dessin} et met à jour
     * l'image réelle à partir de la mémoire tampon
     *
     * @param g objet {@link Graphics} sur lequel dessiner
     */
    @Override
    public void paintComponent(Graphics g)
    {
        paintBuffer((Graphics2D) g);
    }

    /**
     * Dessine les différents composants du jeu
     *
     * @param g2d objet {@link Graphics} sur lequel dessiner
     */
    private void paintBuffer(Graphics2D g2d)
    {
        // paramètres d'affichage (anticrénelage)
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(
            RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(
            RenderingHints.KEY_STROKE_CONTROL,
            RenderingHints.VALUE_STROKE_NORMALIZE);

        // effacement de l'écran
        g2d.setColor(BACKGROUND);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        var center = mapToScreen(Position.ZERO);
        g2d.setColor(Color.BLACK);
        g2d.drawLine(center.x, 0, center.x, getHeight());
        g2d.drawLine(0, center.y, getWidth(), center.y);

        drawGame(g2d);
    }

    /**
     * Dessine le jeu
     *
     * @param g objet {@link Graphics} sur lequel dessiner
     */
    private void drawGame(Graphics2D g)
    {
        var goal = model.getGoal();
        if (goal instanceof RegattaGoal)
        {
            var rg = (RegattaGoal) goal;
            Checkpoint[] checkpoints = rg.getCheckpoints();
            for (int i = 0; i < checkpoints.length; i++)
            {
                drawCheckpoint(g, checkpoints[i], i);
            }
        }

        if (np != null)
        {
            for (ShapedEntity visibleEntity : getVisibleShapes())
            {
                drawEntity(g, visibleEntity);
            }
        }

        drawEntity(g, model.getShip());

        drawShipDeck(g, model.getShip(), model.getSailors());

        drawShipVision(g, model.getShip());

        drawNodes(g);

        drawShipHistory(g);

        if (shipHistory.isEmpty() || !model.getShip().getPosition().equals(shipHistory.get(shipHistory.size() - 1)))
        {
            shipHistory.add(model.getShip().getPosition());
        }

        drawLegendText(g);
    }

    private void drawNodes(Graphics2D g)
    {
        if (!drawPath)
        {
            return;
        }

        g = (Graphics2D) g.create();

        var graph = CheckpointObjective.graph;
        if (graph == null)
        {
            return;
        }

        var nodes = CheckpointObjective.nodes;
        g.setColor(Color.ORANGE);

        var lines = graph.getEdges();

        for (Pair<Node, Node> line : lines)
        {
            var sa = mapToScreen(line.first.position);
            var sb = mapToScreen(line.second.position);
            g.drawLine(sa.x, sa.y, sb.x, sb.y);
        }

        g.setColor(Color.MAGENTA);
        var path = CheckpointObjective.path;
        for (int i = 0; i < path.size() - 1; i++)
        {
            var cur = mapToScreen(path.get(i).position);
            var nex = mapToScreen(path.get(i + 1).position);
            g.drawLine(cur.x, cur.y, nex.x, nex.y);
        }

        g.setColor(Color.BLACK);
        for (Point2d p : nodes)
        {
            drawShape(g, new Circle(10), p.toPosition());
        }
    }

    private void drawLegendText(Graphics2D g)
    {
        g = (Graphics2D) g.create();

        var mouse = this.getMousePosition();
        if (mouse != null)
        {
            var p = mapToWorld(mouse);
            g.drawString(String.format("X = %6.2f", p.getX()), 20, getHeight() - 40);
            g.drawString(String.format("Y = %6.2f", p.getY()), 20, getHeight() - 20);
        }

        if (np.getWind() != null)
        {
            g.setStroke(HISTORY);
            g.setColor(Color.BLACK);
            g.translate(getWidth() - 40, getHeight() - 55);

            g.drawString("Vent", -12, 45);
            g.scale(1, 1);
            g.fillOval(-3, -3, 7, 7);
            g.rotate(np.getWind().getOrientation() + Math.PI);
            g.drawPolygon(new Polygon(new int[] { 0, -7, 7 }, new int[] { 28, 18, 18 }, 3));
            g.drawPolygon(new Polygon(new int[] { -1, 1, 1, 7, 7, 0, -7, -7, -1, -1}, new int[] { 18, 18, -10, -16, -28, -22, -28, -16, -10, 14 }, 10));
        }
    }

    private void drawEntity(Graphics2D g, ShapedEntity se)
    {
        g = (Graphics2D) g.create();

        g.setColor(ENTITY_COLORS.get(se.getClass()));
        drawShape(g, se.getShape(), se.getPosition());
    }

    private void drawShipHistory(Graphics2D g)
    {
        g = (Graphics2D) g.create();

        var x = new int[shipHistory.size()];
        var y = new int[shipHistory.size()];
        for (int i = 0; i < shipHistory.size(); i++)
        {
            var conv = mapToScreen(shipHistory.get(i));
            x[i] = conv.x;
            y[i] = conv.y;
        }
        g.setStroke(HISTORY);
        g.setColor(Color.BLACK);
        g.drawPolyline(x, y, x.length);
    }

    private void drawShipVision(Graphics2D g, Bateau b)
    {
        g = (Graphics2D) g.create();

        g.setStroke(DASHED);
        g.setColor(Color.BLACK);

        var mp = mapToScreen(b.getPosition());
        var md = mapToScreen(1000);
        g.drawOval(mp.x - md, mp.y - md, 2 * md, 2 * md);
    }

    private void drawShipDeck(Graphics2D g, Bateau b, Marin[] sailors)
    {
        g = (Graphics2D) g.create();

        g.translate(DECK_MARGIN, DECK_MARGIN);

        for (OnboardEntity entity : b.getEntities())
        {
            var index = 0;
            if (entity instanceof Voile && ((Voile) entity).isOpenned())
            {
                index = 1;
            }
            var img = ENTITY_ICONS.get(entity.getClass())[index];
            g.drawImage(img, entity.getY() * DECK_GRID_SIZE, entity.getX() * DECK_GRID_SIZE,
                DECK_GRID_SIZE, DECK_GRID_SIZE,
                usedEntities.contains(entity) ? Color.RED : new Color(0, true), null);
        }
        var i = 0;
        for (Marin sailor : sailors)
        {
            var x = sailor.getY() * DECK_GRID_SIZE;
            var y = sailor.getX() * DECK_GRID_SIZE;
            var sh = DECK_GRID_SIZE / 2;
            g.setColor(Color.getHSBColor((float) i / sailors.length, 1, 1));
            g.fillOval(sh + x, sh + y, sh, sh);
            g.setColor(Color.BLACK);
            g.drawString(sailor.getId() + "", sh + x + 3, sh + y + 15);
            i++;
        }

        drawDeckGrid(g, b);
    }

    private void drawDeckGrid(Graphics2D g, Bateau b)
    {
        g.setColor(Color.BLACK);

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
        }
    }

    private void drawCheckpoint(Graphics2D g, Checkpoint c, int i)
    {
        g = (Graphics2D) g.create();
        g.setColor(Color.RED);
        drawShape(g, c.getShape(), c.getPosition());
        g.setColor(Color.BLACK);
        var p = mapToScreen(c.getPosition());
        g.drawString(i + "", p.x - 4, p.y + 4);
    }

    private Point mapToScreen(Point2d p)
    {
        return new Point(getWidth() / 2 + mapToScreen(p.getX() - cameraPos.x),
            this.getHeight() / 2 + mapToScreen(p.getY() - cameraPos.y));
    }

    private Position mapToWorld(Point p)
    {
        return new Position(mapToWorld(p.x - getWidth() / 2) + cameraPos.x,
            mapToWorld(p.y - getHeight() / 2) + cameraPos.y, 0);
    }

    private int mapToScreen(double dist)
    {
        return (int) (dist * scale);
    }

    private double mapToWorld(int dist)
    {
        return dist / scale;
    }

    private void drawShape(Graphics2D g, Shape s, Position p)
    {
        drawShape(g, s, p, false);
    }

    private void drawShape(Graphics2D gt, Shape s, Position p, boolean noRot)
    {
        gt = (Graphics2D) gt.create();
        var mp = mapToScreen(p);
        gt.translate(mp.x, mp.y);

        var gtr = (Graphics2D) gt.create();
        if (s instanceof Circle)
        {
            var c = (Circle) s;
            var rad = mapToScreen(c.getRadius());
            gtr.fillOval(-rad, -rad, 2 * rad, 2 * rad);
            return;
        }

        if (!noRot)
        {
            gtr.rotate(p.getOrientation(), 0, 0);
        }

        if (s instanceof Rectangle)
        {
            var r = (Rectangle) s;
            var h = mapToScreen(r.getWidth());
            var w = mapToScreen(r.getHeight());

            gtr.fillRect(-w / 2, -h / 2, w, h);

        }
        else if (s instanceof fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Polygon)
        {
            var pol = (fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Polygon) s;
            var ap = new Polygon();
            for (Point2d pt : pol.getVertices())
            {
                ap.addPoint(mapToScreen(pt.x), mapToScreen(pt.y));
            }

            gtr.fillPolygon(ap);
        }

        gtr.setStroke(SHAPE_CROSS);
        gtr.setColor(Color.BLACK);
        gtr.drawPolygon(new Polygon(new int[] { -10, -10, 20 }, new int[] { -10, 10, 0 }, 3));
    }

    public void clearHistory()
    {
        shipHistory.clear();
    }

    public void setCockpit(Cockpit cockpit)
    {
        this.cockpit = cockpit;
    }
}
