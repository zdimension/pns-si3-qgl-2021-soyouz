/**
 * Link.java
 * <p>
 * Copyright (c) 2013-2016, F(X)yz
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of F(X)yz, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL F(X)yz BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package fr.unice.polytech.si3.qgl.soyouz.tooling.awt.threed.cloth;

import javafx.geometry.Point3D;

import java.util.logging.Logger;

/**
 * @author Jason Pollastrini aka jdub1581
 */
public class Link implements Constraint
{
    private static final Logger log = Logger.getLogger(Link.class.getName());

    private final double distance, stiffness, damping = 0.75;
    private final WeightedPoint p1, p2;

    public Link(WeightedPoint p1, WeightedPoint p2, double distance, double stiffness)
    {
        this.p1 = p1;
        this.p2 = p2;
        this.distance = distance;
        this.stiffness = stiffness;
    }

    /* Option 2
        // Pseudo-code to satisfy (C2)
        delta = x2-x1;
        deltalength = sqrt(delta*delta);
        diff = (deltalength-restlength)
              /(deltalength*(invmass1+invmass2));
        x1 -= invmass1*delta*diff;
        x2 += invmass2*delta*diff;
    */

    @Override
    public void solve()
    {

        //calculate the distance between the two PointMasss
        Point3D diff = p1.position.subtract(p2.position);

        double d = diff.magnitude();

        double difference = (distance - d) / d;

        double im1 = 1 / p1.getMass();
        double im2 = 1 / p2.getMass();
        double scalarP1 = (im1 / (im1 + im2)) * stiffness;
        double scalarP2 = stiffness - scalarP1;

        synchronized (this)
        {
            p1.position = p1.position.add(diff.multiply(scalarP1 * difference));
            p2.position = p2.position.subtract(diff.multiply(scalarP2 * difference));
        }

    }

    public WeightedPoint getAnchorPoint()
    {
        return p1;
    }

    public WeightedPoint getAttachedPoint()
    {
        return p2;
    }

    @Override
    public String toString()
    {
        return "PointLink{" + "distance=" + distance + ", stiffness=" + stiffness + ", p1=" + p1 + ", p2=" + p2 + '}';
    }

}
