package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Rectangle;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.OnboardEntity;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class SimulatorCanvas extends Canvas
{
    private static final Color BACKGROUND = new Color(202, 219, 255);
    private static final Color BOAT = new Color(193, 169, 134);
    private static final Color OBSTACLE = new Color(102, 186, 90);

    private static final Map<Class<?>, Image> ENTITY_ICONS;
    private static final double SCALE = 1;
    private static final int MARGIN = 40;
    private static final int DECK_GRID_SIZE = 40;
    private static final int DECK_MARGIN = 30;

    static
    {
        ENTITY_ICONS = Map.of(
            Rame.class, "paddle.png",
            Marin.class, "sailor.png"
        ).entrySet().stream().map(e ->
        {
            try
            {
                return Map.entry(e.getKey(), ImageIO.read(SimulatorCanvas.class.getResource(e.getValue())));
            }
            catch (IOException ioException)
            {
                throw new RuntimeException(ioException);
            }
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Image virtuelle de dessin
     */
    private Image dbImage;
    /**
     * Objet <code>Graphics</code> correspond à {@link #dbImage l'image de dessin}
     */
    private Graphics dbGraphics;
    private InitGameParameters model;

    public SimulatorCanvas(InitGameParameters model)
    {
        this.model = model;
    }

    /**
     * Appelle la {@link #paintBuffer(Graphics2D) fonction principale de dessin} et met à jour l'image réelle à partir de la mémoire tampon
     *
     * @param g objet {@link Graphics} sur lequel dessiner
     */
    @Override
    public void paint(Graphics g)
    {
        // met à jour l'image virtuelle et la recrée si besoin
        if (dbImage == null ||
            this.getWidth() != dbImage.getWidth(this)
            || this.getHeight() != dbImage.getHeight(this))
        {
            if (dbGraphics != null)
            {
                dbGraphics.dispose();
            }

            if (dbImage != null)
            {
                dbImage.flush();
            }

            System.gc();

            dbImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
            dbGraphics = dbImage.getGraphics();
        }

        // met à jour l'affichage réel
        if (dbGraphics != null)
        {
            dbGraphics.clearRect(0, 0, dbImage.getWidth(this), dbImage.getHeight(this));

            paintBuffer((Graphics2D) dbGraphics);

            g.drawImage(dbImage, 0, 0, this);
        }
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
            for (Checkpoint checkpoint : rg.getCheckpoints())
            {
                drawCheckpoint(g, checkpoint);
            }
        }

        drawShip(g, model.getShip());

        drawShipDeck(g, model.getShip(), model.getSailors());
    }

    private void drawShipDeck(Graphics2D g, Bateau b, Marin[] sailors)
    {
        g = (Graphics2D) g.create();

        var w = b.getDeck().getWidth();
        var rw = w * DECK_GRID_SIZE;
        var h = b.getDeck().getLength();
        var rh = h * DECK_GRID_SIZE;

        g.setColor(Color.BLACK);
        g.translate(DECK_MARGIN, DECK_MARGIN);
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
        for (OnboardEntity entity : b.getEntities())
        {
            var img = ENTITY_ICONS.get(entity.getClass());
            g.drawImage(img, entity.getY() * DECK_GRID_SIZE, entity.getX() * DECK_GRID_SIZE, DECK_GRID_SIZE, DECK_GRID_SIZE, null);
        }
        var simg = ENTITY_ICONS.get(Marin.class);
        for (Marin sailor : sailors)
        {
            g.drawImage(simg, sailor.getY() * DECK_GRID_SIZE + DECK_GRID_SIZE / 2, sailor.getX() * DECK_GRID_SIZE + DECK_GRID_SIZE / 2, DECK_GRID_SIZE / 2, DECK_GRID_SIZE / 2, null);
        }
    }

    private void drawShip(Graphics2D g, Bateau b)
    {
        g.setColor(BOAT);
        drawShape(g, b.getShape(), b.getPosition());
    }

    private void drawCheckpoint(Graphics2D g, Checkpoint c)
    {
        g.setColor(Color.RED);
        drawShape(g, c.getShape(), c.getPosition());
    }

    private Point mapToScreen(Position p)
    {
        return new Point(mapToScreen(p.getX()) + MARGIN, this.getHeight() - mapToScreen(p.getY()) - MARGIN);
    }

    private int mapToScreen(double dist)
    {
        return (int) (dist * SCALE);
    }

    private void drawShape(Graphics2D g, Shape s, Position p)
    {
        drawShape(g, s, p, false);
    }

    private void drawShape(Graphics2D g, Shape s, Position p, boolean noRot)
    {
        g = (Graphics2D) g.create();
        var mp = mapToScreen(p);
        if (s instanceof Circle)
        {
            var c = (Circle) s;
            var rad = mapToScreen(c.getRadius());
            g.fillOval(mp.x - rad / 2, mp.y - rad / 2, rad, rad);
        }
        else if (s instanceof Rectangle)
        {
            var r = (Rectangle) s;
            var h = mapToScreen(r.getWidth()) * 10;
            var w = mapToScreen(r.getHeight()) * 10;
            g.translate(mp.x, mp.y);
            if (!noRot)
            {
                g.rotate(-(p.getOrientation() + r.getOrientation()), 0, 0);
            }
            g.fillRect(-w / 2, -h / 2, w, h);
        }
    }
}
