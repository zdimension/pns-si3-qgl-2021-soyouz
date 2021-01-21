package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("OAR")
public class OarAction extends GameAction
{
    private int sailorId;
}
