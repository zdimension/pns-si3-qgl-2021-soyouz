package fr.unice.polytech.si3.qgl.soyouz;

import org.aspectj.lang.Signature;

import java.util.logging.Logger;

public aspect TraceAspect
{
    private static final Logger logger = Logger.getLogger(TraceAspect.class.getSimpleName());

    pointcut traceMethods() : (execution(* *(..))&& !cflow(within(Trace)));

    before(): traceMethods(){
        Signature sig = thisJoinPointStaticPart.getSignature();
        String line =""+ thisJoinPointStaticPart.getSourceLocation().getLine();
        String sourceName = thisJoinPointStaticPart.getSourceLocation().getWithinType().getCanonicalName();
        System.out.println(
                "Call from "
                        +  sourceName
                        +" line " +
                        line
                        +" to " +sig.getDeclaringTypeName() + "." + sig.getName()
        );
    }
}