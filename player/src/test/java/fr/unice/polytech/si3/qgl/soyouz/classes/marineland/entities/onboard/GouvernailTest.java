package fr.unice.polytech.si3.qgl.soyouz.classes.marineland.entities.onboard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class GouvernailTest
{
    Gouvernail gouvernail;
    @BeforeEach
    void init()
    {
        gouvernail = new Gouvernail(1,1);
    }

    @Test
    void rangeTest(){
        assertEquals(-0.78539816339,Gouvernail.ALLOWED_ROTATION.first);
        assertEquals(0.78539816339,Gouvernail.ALLOWED_ROTATION.second);
    }
}
