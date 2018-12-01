package ro.luca1152.balloon.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import ro.luca1152.balloon.MyGame;

class Finish extends Image {
    // Collisions
    private Rectangle collisionBox;

    Finish(Rectangle rectangle) {
        super(MyGame.manager.get("textures/finish.png", Texture.class));
        // Image
        setPosition(rectangle.x / MyGame.PPM, rectangle.y / MyGame.PPM);
        setSize(rectangle.width / MyGame.PPM, rectangle.height / MyGame.PPM);

        // Collisions
        collisionBox = new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    Rectangle getCollisionBox() {
        return collisionBox;
    }
}
