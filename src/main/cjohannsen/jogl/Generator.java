package cjohannsen.jogl;

import com.jogamp.opengl.GLAutoDrawable;

import java.util.List;

/**
 * Created by cjohannsen on 7/12/15.
 */
public interface Generator {

    void reset();

    void generate();

    void render(GLAutoDrawable drawable);

}
