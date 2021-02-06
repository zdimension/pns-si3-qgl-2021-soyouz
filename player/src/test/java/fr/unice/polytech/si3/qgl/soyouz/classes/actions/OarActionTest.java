package fr.unice.polytech.si3.qgl.soyouz.classes.actions;

import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.Marin;
import fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard.Rame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OarActionTest {

    Marin sailor;
    OarAction oarAction;

    @BeforeEach
    void init() {
        sailor = new Marin(1, 0, 0, "Tom Pouce");
        oarAction = new OarAction(sailor);
    }

    @Disabled
    @Test
    void isUsedTest(){
        //assertTrue();
    }
}