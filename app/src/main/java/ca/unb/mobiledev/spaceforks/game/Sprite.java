package ca.unb.mobiledev.spaceforks.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Sprite {
    public Vector position;
    public Vector velocity;
    public double rotation;
    public Collision imageBounds;
    public Bitmap image;

    public boolean wrapOn = false;
    public boolean edgeOn = false;

    public boolean offScreen = false;
    public int points = 0;
    public int level = 1;

    private double screenWidth;
    private double screenHeight;

    public Sprite(){
        position = new Vector();
        velocity = new Vector();
        rotation = 0;
        imageBounds = new Collision();
    }

    public Sprite(Bitmap bmp){
        this();
        this.setImage(bmp);
    }

    public void setImage(Bitmap bmp){
        image = bmp;
        imageBounds.setSize(image.getWidth(), image.getHeight());
    }

    public Collision getImageBounds(){
        imageBounds.setPosition(position.x, position.y);
        return imageBounds;
    }

    // Checks for collision between two sprites
    public boolean collision(Sprite obj){
        return this.getImageBounds().collision(obj.getImageBounds());
    }

    public void update(){
        spriteOffScreen();
        if(wrapOn)
            this.wrap();
        if(edgeOn)
            this.screenEdge();
        if(!wrapOn && !edgeOn)
            position.add(velocity.x, velocity.y);
        spriteOffScreen();
    }

    // draw and rotate sprite to the canvas
    public void draw(Canvas canvas){
        canvas.save();
        canvas.rotate((float)rotation,
                (float)(position.x + (image.getWidth()/2)),
                (float)(position.y + (image.getWidth()/2)));
        canvas.drawBitmap(image, (int)position.x, (int)position.y, null);
        canvas.restore();
    }

    // sets the size of the screen
    public void setScreenSize(double screenWidth, double screenHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    // Limits sprite to the screen area
    public void screenEdge(){
        // if in a corner
        if(((position.x > screenWidth - image.getWidth()) || (position.x < 0)) &&
                ((position.y > screenHeight - image.getHeight()) || (position.y < 0))){
            position.add(0,0);

            // Top right corner
            if((position.x > screenWidth - image.getWidth()) && (position.y < 0)){
                if(velocity.x < -1 && velocity.y > 1)
                    position.add(velocity.x, velocity.y);
            }

            // Bottom right corner
            if((position.x > screenWidth - image.getWidth()) && (position.y > screenHeight - image.getHeight())){
                if(velocity.x < -1 && velocity.y < -1)
                    position.add(velocity.x, velocity.y);
            }

            // Top left corner
            if((position.x < 0) && (position.y < 0)){
                if(velocity.x > 1 && velocity.y > 1)
                    position.add(velocity.x, velocity.y);
            }

            // Bottom left corner
            if((position.x < 0) && (position.y > screenHeight - image.getHeight())){
                if(velocity.x > 1 && velocity.y < -1)
                    position.add(velocity.x, velocity.y);
            }
        }
        // if the player has reached a X bound can still move on y axis
        else if((position.x > screenWidth - image.getWidth()) || (position.x < 0)){
            position.add(0, velocity.y);

            // if player is at the right of the screen (highest x value)
            if((velocity.x < -1) && (position.x > screenWidth - image.getWidth()))
                position.add(velocity.x, velocity.y);

            // if player is at the left of screen (lowest x value)
            else if((velocity.x > 1) && (position.x < 0))
                position.add(velocity.x, velocity.y);
        }
        // if the player has reached a Y bound can still move on the x axis
        else if((position.y > screenHeight - image.getHeight()) || (position.y < 0)){
            position.add(velocity.x, 0);

            // if player is at the bottom of the screen (highest Y value)
            if((velocity.y < -1) && position.y > (screenHeight - image.getHeight()))
                position.add(velocity.x, velocity.y);

            // if player is at the top of the screen (lowest Y value)
            else if((velocity.y > 1) && (position.y < 0))
                position.add(velocity.x, velocity.y);
        }
        else
            position.add(velocity.x, velocity.y);
    }

    // Wraps the sprite to the other side of the screen when screen is left
    public void wrap(){
        // Move Object
        position.add(velocity.x, velocity.y);

        // Check bounds
        if(position.x + image.getWidth() < 0)
            position.x = screenWidth;

        if(position.x > screenWidth)
            position.x = -image.getWidth();

        if(position.y + image.getHeight() < 0)
            position.y =screenHeight;

        if(position.y > screenHeight)
            position.y = -image.getHeight();
    }

    // Returns true if the sprite is completely off the screen
    public void spriteOffScreen(){
        if((position.x + image.getWidth() < 0) ||
                (position.x > screenWidth) ||
                (position.y + image.getHeight() < 0) ||
                (position.y > screenHeight))
            offScreen = true;
        else
            offScreen = false;
    }

    // Used to add score points to the sprite, only used for the player sprite
    public void addPoints(int points){
        this.points += points;
    }

    // sets the level of the sprite, just used for tracking similar sprites
    public void setLevel(int level){
        this.level = level;
    }
}
