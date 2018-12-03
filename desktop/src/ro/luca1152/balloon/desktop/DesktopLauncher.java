package ro.luca1152.balloon.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ro.luca1152.balloon.MyGame;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.initialBackgroundColor = MyGame.backgroundWhite;
        config.title = "Balloons";
        config.width = 640;
        config.height = 640;
        config.samples = 4;
        new LwjglApplication(new MyGame(), config);
    }
}
