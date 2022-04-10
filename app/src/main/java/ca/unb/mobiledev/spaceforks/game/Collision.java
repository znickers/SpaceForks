package ca.unb.mobiledev.spaceforks.game;

public class Collision {
    public double x;
    public double y;
    public double width;
    public double height;

    public Collision(){
        this.setPosition(0,0);
        this.setSize(1,1);
    }

    public Collision(double x, double y, double width, double height){
        this.setPosition(x,y);
        this.setSize(width,height);
    }

    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void setSize(double width, double height){
        this.width = width;
        this.height = height;
    }

    public boolean collision(Collision obj){
        boolean noCollision = x + width < obj.x ||
                obj.x + obj.width < x ||
                y + height < obj.y ||
                obj.y + obj.height < y;
        return !noCollision;
    }
}
