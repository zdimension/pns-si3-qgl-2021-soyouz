package fr.unice.polytech.si3.qgl.soyouz.tooling.awt;

import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.Checkpoint;
import fr.unice.polytech.si3.qgl.soyouz.classes.gameflow.goals.RegattaGoal;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.Position;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Circle;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Rectangle;
import fr.unice.polytech.si3.qgl.soyouz.classes.geometry.shapes.Shape;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.Bateau;
import fr.unice.polytech.si3.qgl.soyouz.classes.parameters.InitGameParameters;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SimulatorCanvas extends Canvas
{
    private static final Color BACKGROUND = new Color(202, 219, 255);
    private static final Color BOAT = new Color(193, 169, 134);
    private static final Color OBSTACLE = new Color(102, 186, 90);

    private static final double SCALE = 1;
    private static final int MARGIN = 40;

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
                dbGraphics.dispose();

            if (dbImage != null)
                dbImage.flush();

            System.gc();

            dbImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
            dbGraphics = dbImage.getGraphics();
        }

        // met à jour l'affichage réel
        if (dbGraphics != null)
        {
            dbGraphics.clearRect(0, 0, dbImage.getWidth(this), dbImage.getHeight(this));

            paintBuffer((Graphics2D)dbGraphics);

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
     * @param g    objet {@link Graphics} sur lequel dessiner
     */
    private void drawGame(Graphics2D g)
    {
        var goal = model.getGoal();
        if (goal instanceof RegattaGoal)
        {
            var rg = (RegattaGoal)goal;
            for (Checkpoint checkpoint : rg.getCheckpoints())
            {
                drawCheckpoint(g, checkpoint);
            }
        }

        drawShip(g, model.getShip());
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
        return (int)(dist * SCALE);
    }

    private void drawShape(Graphics2D g, Shape s, Position p)
    {
        g = (Graphics2D)g.create();
        var mp = mapToScreen(p);
        if (s instanceof Circle)
        {
            var c = (Circle)s;
            var rad = mapToScreen(c.getRadius());
            g.fillOval(mp.x - rad / 2, mp.y - rad / 2, rad, rad);
        }
        else if (s instanceof Rectangle)
        {
            var r = (Rectangle)s;
            var h = mapToScreen(r.getWidth()) * 10;
            var w = mapToScreen(r.getHeight()) * 10;
            g.translate(mp.x, mp.y);
            g.rotate(-(p.getOrientation() + r.getOrientation()), 0, 0);
            g.fillRect(-w / 2, -h / 2, w, h);
        }
    }
}
