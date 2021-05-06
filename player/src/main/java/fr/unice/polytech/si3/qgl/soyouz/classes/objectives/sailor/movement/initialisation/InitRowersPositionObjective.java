package fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.initialisation;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.CompositeObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.MovingObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.SailorMovementObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.objectives.sailor.movement.SailorXMovementObjective;
import fr.unice.polytech.si3.qgl.soyouz.classes.types.LineOnBoat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Initialise the position of every rowers on their dedicated line or oar.
 */
public class InitRowersPositionObjective extends CompositeObjective<MovingObjective> implements MovingObjective
{
    private final List<Marin> sailors;
    private final List<LineOnBoat> linesOnBoatWithOars;
    private final List<LineOnBoat> linesOnBoatWithOneOars;

    /**
     * Constructor.
     *
     * @param rowers All sailors disposed to oar.
     */
    public InitRowersPositionObjective(List<Marin> rowers, List<LineOnBoat> linesOnBoat)
    {
        linesOnBoatWithOars = new ArrayList<>();
        linesOnBoatWithOneOars = new ArrayList<>();
        setLinesOnBoatWithOars(linesOnBoat);

        sailors = rowers;

        int sailorsExceeding = sailors.size() - linesOnBoatWithOars.size();
        sailorsExceeding = Math.max(sailorsExceeding, 0);

        generateSubObjectives(sailorsExceeding);

    }

    /**
     * Determine all lines with one or two oars.
     *
     * @param linesOnBoat All the lines that compose the Deck.
     */
    private void setLinesOnBoatWithOars(List<LineOnBoat> linesOnBoat)
    {
        linesOnBoatWithOars.addAll(linesOnBoat.stream().filter(line -> !line.getOars().isEmpty()).collect(Collectors.toList()));
        linesOnBoatWithOneOars.addAll(linesOnBoat.stream().filter(line -> line.getOars().size() == 1).collect(Collectors.toList()));
        Collections.sort(linesOnBoatWithOars);
        Collections.sort(linesOnBoatWithOneOars);
    }

    /**
     * Generate all sub objectives AKA x-placement objectives.
     *
     * @param nbSailorsExceeding The number of sailor in addition to the number of lines.
     */
    private void generateSubObjectives(int nbSailorsExceeding)
    {
        int nbSailorPlaced = 0;
        for (LineOnBoat line : linesOnBoatWithOars)
        {
            if (nbSailorPlaced >= sailors.size()) return;
            if (linesOnBoatWithOneOars.contains(line))
            {
                children.add(new SailorMovementObjective(
                    sailors.get(nbSailorPlaced), line.getOars().get(0).getPos()));
                nbSailorPlaced++;
            }
            else
            {
                if (nbSailorsExceeding > 0)
                {
                    generateSubObjectiveForTwoSailorsOnTheSameLine(line,
                        sailors.get(nbSailorPlaced),
                        sailors.get(nbSailorPlaced + 1));
                    nbSailorPlaced += 2;
                    nbSailorsExceeding--;
                }
                else
                {
                    children.add(new SailorXMovementObjective(
                        sailors.get(nbSailorPlaced), line.getX()));
                    nbSailorPlaced++;
                }
            }
        }
    }

    /**
     * Move each rowers on the same line on their dedicater oar based on their Y position.
     *
     * @param line    The current line.
     * @param sailor1 The first sailor of the line.
     * @param sailor2 The second sailor of the line.
     */
    private void generateSubObjectiveForTwoSailorsOnTheSameLine(LineOnBoat line, Marin sailor1,
                                                                Marin sailor2)
    {
        Marin sailorLeft = (sailor1.getY() < sailor2.getY()) ? sailor1 : sailor2;
        Marin sailorRight = sailorLeft.equals(sailor1) ? sailor2 : sailor1;
        List<Rame> oars = line.getOars();
        Rame oarLeft = oars.get(0).getY() == 0 ? oars.get(0) : oars.get(1);
        Rame oarRight = oarLeft.equals(oars.get(0)) ? oars.get(1) : oars.get(0);
        children.add(new SailorMovementObjective(sailorLeft, oarLeft.getPos()));
        children.add(new SailorMovementObjective(sailorRight, oarRight.getPos()));
    }
}