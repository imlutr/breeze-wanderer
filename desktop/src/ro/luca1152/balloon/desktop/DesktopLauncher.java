package ro.luca1152.balloon.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ro.luca1152.balloon.MyGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Balloon";
        config.width = 600;
        config.height = 600;
        config.samples = 4;
        new LwjglApplication(new MyGame(), config);
    }
}
