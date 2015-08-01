package cjohannsen.jogl;

import java.awt.*;

/**
 * Created by cjohannsen on 7/12/15.
 */
public class Vertex {

    public float x;
    public float y;
    public float z;
    public Color color;

    public Vertex() {
        x = 0;
        y = 0;
        z = 0;
        color = Color.white;
    }

    public Vertex(float x, float y, float z, Color color) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
    }
}
