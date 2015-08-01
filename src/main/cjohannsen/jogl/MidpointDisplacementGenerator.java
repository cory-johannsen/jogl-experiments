package cjohannsen.jogl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import java.awt.*;

/**
 * Created by cjohannsen on 7/12/15.
 */
public class MidpointDisplacementGenerator implements Generator {



    private static final Color[] colors = {
            new Color(0, 102, 0), // Dark green
            new Color(0, 153, 0), // Medium green
            new Color(0, 204, 0), // Green
            new Color(102, 51, 0), // Dark brown
            new Color(153, 76, 0), // Brown
            new Color(96, 96, 96), // Grey
            new Color(192, 192, 192), // Light grey
            new Color(255, 255, 255) // White
    };

    public static final Vertex[] DEFAULT_VERTEX_LIST = {
            new Vertex(-10.0f, -10.0f, 0.0f, colors[0]),
            new Vertex(0.0f, -10.0f, 0.0f, colors[0]),
            new Vertex(10.0f, -10.0f, 0.0f, colors[0]),

            new Vertex(-10.0f, 0.0f, 0.0f, colors[0]),
            new Vertex(0.0f, 0.0f, 7.5f, colors[7]),
            new Vertex(10.0f, 0.0f, 0.0f, colors[0]),

            new Vertex(-10.0f, 10.0f, 0.0f, colors[0]),
            new Vertex(0.0f, 10.0f, 0.0f, colors[0]),
            new Vertex(10.0f, 10.0f, 0.0f, colors[0])
    };

    private Vertex[][] vertices;
    private int rowCount = 3;
    private int columnCount = 3;
    private int iteration = 0;

    float xMin = -10.0f;
    float xMax = 10.0f;
    float yMin = -10.0f;
    float yMax = 10.0f;
    float zMin = 0.0f;
    float zMax = 10.0f;

    private float xNoiseFactor = 0.5f;
    private float yNoiseFactor = 0.5f;
    private float zNoiseFactor = 1.0f;

    private float xRotation = -75.0f;
    private float yRotation = 0.0f;
    private float zRotation = 0.0f;
    
    public MidpointDisplacementGenerator() {
        reset();
    }

    @Override
    public void reset() {
        rowCount = 5;
        columnCount = 5;
        iteration = 0;

        xNoiseFactor = 0.5f;
        yNoiseFactor = 0.5f;
        zNoiseFactor = 1.0f;

        xRotation = -75.0f;
        yRotation = 0.0f;
        zRotation = 0.0f;
        initializeVertices();
    }

    @Override
    public void generate() {
        iteration++;
        int newRowCount = (rowCount * 2) - 1;
        int newColumnCount = (columnCount * 2) - 1;
        Vertex[][] newVertices = new Vertex[newRowCount][newColumnCount];
        int newRowIndex = 0;
        for(int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            int newColumnIndex = 0;
            newVertices[newRowIndex] = new Vertex[newColumnCount];
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                Vertex currentVertex = vertices[rowIndex][columnIndex];
                if (columnIndex > 0) {
                    Vertex previousVertex = vertices[rowIndex][columnIndex - 1];
                    newVertices[newRowIndex][newColumnIndex++] = createVertex(previousVertex, currentVertex);
                }
                newVertices[newRowIndex][newColumnIndex++] = currentVertex;
            }
            newRowIndex += 2;
        }

        for(int rowIndex = 1; rowIndex < newRowCount; rowIndex += 2) {
            for (int columnIndex = 0; columnIndex < newColumnCount; columnIndex++) {
                Vertex lowerVertex = newVertices[rowIndex - 1][columnIndex];
                Vertex upperVertex = newVertices[rowIndex + 1][columnIndex];
                newVertices[rowIndex][columnIndex] = createVertex(lowerVertex, upperVertex);
            }
        }

        vertices = newVertices;
        rowCount = newRowCount;
        columnCount = newColumnCount;
    }

    @Override
    public void render(GLAutoDrawable drawable) {

        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT); // clear color and depth buffers
        gl.glLoadIdentity();  // reset the model-view matrix

        gl.glTranslatef(0.0f, 0.0f, -30.0f); // translate into the screen

        gl.glRotatef(xRotation, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(yRotation, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(zRotation, 0.0f, 0.0f, 1.0f);

        gl.glBegin(GL2.GL_QUADS);
        for(int rowIndex = 1; rowIndex < vertices.length; rowIndex++) {
            Vertex[] previousRow = vertices[rowIndex - 1];
            Vertex[] currentRow = vertices[rowIndex];
            for (int columnIndex = 1; columnIndex < currentRow.length; columnIndex++) {
                Vertex topLeft = previousRow[columnIndex - 1];
                Vertex topRight = previousRow[columnIndex];
                Vertex bottomLeft = currentRow[columnIndex - 1];
                Vertex bottomRight = currentRow[columnIndex];
                float[] v1rgb = topLeft.color.getColorComponents(null);
                gl.glColor3f(v1rgb[0], v1rgb[1], v1rgb[2]);
                gl.glVertex3f(topLeft.x, topLeft.y, topLeft.z);

                float[] v2rgb = topRight.color.getColorComponents(null);
                gl.glColor3f(v2rgb[0], v2rgb[1], v2rgb[2]);
                gl.glVertex3f(topRight.x, topRight.y, topRight.z);

                float[] v3rgb = bottomRight.color.getColorComponents(null);
                gl.glColor3f(v3rgb[0], v3rgb[1], v3rgb[2]);
                gl.glVertex3f(bottomRight.x, bottomRight.y, bottomRight.z);

                float[] v4rgb = bottomLeft.color.getColorComponents(null);
                gl.glColor3f(v4rgb[0], v4rgb[1], v4rgb[2]);
                gl.glVertex3f(bottomLeft.x, bottomLeft.y, bottomLeft.z);
            }
        }
        gl.glEnd();

        zRotation = (zRotation + 0.5f) % 360.0f;
    }

    private Vertex createVertex(Vertex v1, Vertex v2) {
        float x = (v1.x + v2.x) / 2.0f;
        float y = (v1.y + v2.y) / 2.0f;
        float z = (v1.z + v2.z) / 2.0f;
        float xNoise = (float) (xNoiseFactor * Math.random());
        if (Math.random() > 0.5) {
            xNoise *= -1.0;
        }
        float yNoise = (float) (yNoiseFactor * Math.random());
        if (Math.random() > 0.5) {
            yNoise *= -1.0;
        }
        float zNoise = (float) (zNoiseFactor * Math.random());
        if (Math.random() > 0.5) {
            zNoise *= -1.0;
        }
        return new Vertex(x + xNoise, y + yNoise, z + zNoise, getVertexColor(z + zNoise));
    }

    private void initializeVertices() {
        vertices = new Vertex[rowCount][columnCount];
        int index = 0;
        float xRange = xMax - xMin;
        float yRange = yMax - yMin;
        float zRange = zMax - zMin;
        for (int row = 0; row < rowCount; row++) {
            float y = yMin + (((float) row / (float) rowCount) * yRange);
            for (int column = 0; column < columnCount; column++) {
                float x = xMin + (((float) column / (float) columnCount) * xRange);
                float z = (float)(zRange * Math.random());
                vertices[row][column] = new Vertex(x, y, z, getVertexColor(z));
            }
        }
    }

    private Color getVertexColor(float height) {
        int index = (int)height;
        if (index < 0) {
            index = 0;
        }
        if (index >= colors.length) {
            index = colors.length - 1;
        }
        return colors[index];
    }
}
