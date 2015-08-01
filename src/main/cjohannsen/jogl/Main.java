package cjohannsen.jogl;

/**
 * Created by cjohannsen on 7/11/15.
 */
public class Main {

    public static void main(String[] args) {
        Generator generator = new MidpointDisplacementGenerator();
        ApplicationFrame applicationFrame = new ApplicationFrame(generator);

        applicationFrame.setVisible(true);
    }

}