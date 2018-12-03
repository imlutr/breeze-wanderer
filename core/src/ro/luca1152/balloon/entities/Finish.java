package ro.luca1152.balloon.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import ro.luca1152.balloon.MyGame;

class Finish extends Group {
    // Collisions
    private Rectangle collisionBox;

    Finish(Rectangle information) {
        // Highlight
        Highlight highlight = new Highlight(information, MyGame.finishGreen);
        addActor(highlight);

        // Center
        Image center = new Image(MyGame.manager.get("textures/finish-center.png", Texture.class));
        center.setSize(center.getWidth() / MyGame.PPM, center.getHeight() / MyGame.PPM);
        center.setPosition(information.x + information.width / 2f - center.getWidth() / 2f, information.y + information.height / 2f - center.getHeight() / 2f);
        center.setColor(MyGame.finishGreen);
        addActor(center);

        // Collisions
        collisionBox = new Rectangle(information.getX(), information.getY(), information.width, information.height);
    }

    Rectangle getCollisionBox() {
        return collisionBox;
    }
}
