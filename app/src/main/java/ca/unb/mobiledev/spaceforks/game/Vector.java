package ca.unb.mobiledev.spaceforks.game;

public class Vector {

    public double x;
    public double y;

    public Vector(){
        this.set(0,0);
    }

    public Vector(double x, double y){
        this.set(x,y);
    }

    public void set(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void add(double xVal, double yVal){
        x += xVal;
        y += yVal;
    }

    public void multiply(double val){
        x *= val;
        y *= val;
    }

    public double getLength(){
        return Math.sqrt((x * x) + (y * y));
    }

    public void setLength(double val){
        double length = this.getLength();

        if(length == 0){
            this.set(val, 0);
        }
        else{
            // sets length to 1 then scales it by val
            this.multiply(1/length);
            this.multiply(val);
        }
    }

    public double getAngle(){
        return Math.toDegrees(Math.atan2(y,x));
    }

    public void setAngle(double degrees){
        double length = this.getLength();
        double radians = Math.toRadians(degrees-90);
        x = length * Math.cos(radians);
        y = length * Math.sin(radians);
    }
}
