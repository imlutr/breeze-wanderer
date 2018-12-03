package ro.luca1152.balloon.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import ro.luca1152.balloon.MyGame;

class Finish extends Group {
    // Images
    private Image botLeft, botRight, topLeft, topRight, center;

    // Collisions
    private Rectangle collisionBox;

    Finish(Rectangle rectangle) {
        // Add images
        botLeft = new Image(MyGame.manager.get("textures/finish-bl.png", Texture.class));
        botLeft.setSize(botLeft.getWidth() / MyGame.PPM, botLeft.getHeight() / MyGame.PPM);
        botLeft.setPosition(rectangle.x, rectangle.y);
        botRight = new Image(MyGame.manager.get("textures/finish-br.png", Texture.class));
        botRight.setSize(botRight.getWidth() / MyGame.PPM, botRight.getHeight() / MyGame.PPM);
        botRight.setPosition(rectangle.x + rectangle.width - botRight.getWidth(), rectangle.y);
        topLeft = new Image(MyGame.manager.get("textures/finish-tl.png", Texture.class));
        topLeft.setSize(topLeft.getWidth() / MyGame.PPM, topLeft.getHeight() / MyGame.PPM);
        topLeft.setPosition(rectangle.x, rectangle.y + rectangle.height - topLeft.getHeight());
        topRight = new Image(MyGame.manager.get("textures/finish-tr.png", Texture.class));
        topRight.setSize(topRight.getWidth() / MyGame.PPM, topRight.getHeight() / MyGame.PPM);
        topRight.setPosition(rectangle.x + rectangle.width - topRight.getWidth(), rectangle.y + rectangle.height - topRight.getHeight());
        center = new Image(MyGame.manager.get("textures/finish-center.png", Texture.class));
        center.setSize(center.getWidth() / MyGame.PPM, center.getHeight() / MyGame.PPM);
        center.setPosition(rectangle.x + rectangle.width / 2f - center.getWidth() / 2f, rectangle.y + rectangle.height / 2f - center.getHeight() / 2f);

        // Image
        setPosition(rectangle.x, rectangle.y);
        setSize(rectangle.width, rectangle.height);

        // Collisions
        collisionBox = new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        if (stage != null) {
            stage.addActor(botLeft);
            stage.addActor(botRight);
            stage.addActor(topLeft);
            stage.addActor(topRight);
            stage.addActor(center);
        }
    }

    Rectangle getCollisionBox() {
        return collisionBox;
    }
}
