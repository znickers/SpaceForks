package ca.unb.mobiledev.spaceforks.game;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import ca.unb.mobiledev.spaceforks.MainMenuActivity;
import ca.unb.mobiledev.spaceforks.R;
import ca.unb.mobiledev.spaceforks.fragments.PauseMenuFragment;
import ca.unb.mobiledev.spaceforks.GameOverActivity;

// this class is where the canvas(background which everything is drawn on) is updated and things are
// added to it
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    public static final String TAG = "GameView";
    public static final String FRAGMENT_TAG = "PauseMenuFragment";
    private GameThread thread;
    private Context parentContext;
    private FragmentManager fragmentManager;

    // Sprites
    private Sprite player;
    private int lives = 3;
    private Sprite pause;
    private Sprite background;
    private ArrayList<Sprite> shotList;
    private ArrayList<Sprite> rockList;
    private int maxRocks = 1;

    // Screen related variables
    private Paint textPaint;
    private double screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private double screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    // Accelerometer related variables
    private SensorManager sensorManager;
    private Sensor sensor;

    // PLayer movement from accelerometer
    private float accelX = 0;
    private float accelY = 0;

    // Sensor event listener will be called when sensor event is noticed
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            //Y for spinning (rotating device on side)
            //X for forward or backwards
            //Will need to make a set default position thing eventually
            //Filter out gravity and see what happens
            accelX = sensorEvent.values[0];
            accelY = sensorEvent.values[1];
            //Log.d("Sensor Changed", "Vals:\tX: " + sensorEvent.values[0] +
            //        "\tY: " + sensorEvent.values[1] + "\n\tZ: " + sensorEvent.values[2]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    public GameView(Context context) {
        super(context);
        // Setting up thread and surface
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        parentContext = context;
        setFocusable(true);

        // Setting up the sensor and sensor manager and opening a listener (will need to close at some point)
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Setting up the text paint
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30);

        // Create arraylists for shots and rocks
        shotList = new ArrayList<Sprite>();
        rockList = new ArrayList<Sprite>();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    //Called when creating the surface
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();

        // Create player and set to center of screen
        player = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.rocket));
        player.setScreenSize(screenWidth, screenHeight);
        player.position.set((screenWidth/2) - (player.image.getWidth()/2),
                (screenHeight/2) - (player.image.getHeight()/2));
        player.edgeOn = true;

        // Sprite for pause button
        pause = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.pause));
        pause.setScreenSize(screenWidth, screenHeight);
        pause.position.set((screenWidth - 105), 5);

        // Sprite for background
        background = new Sprite(BitmapFactory.decodeResource(getResources(), R.drawable.background1));
        background.position.set(0,-10);
        background.setImage(Bitmap.createScaledBitmap(background.image, (int)screenWidth, (int)screenHeight+10, false));
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    //Use to update events on the screen
    public void update() throws InterruptedException {

        // Player rotation based on phone Y tilt
        player.rotation = (int)accelY*25;
        player.velocity.setAngle(player.rotation);

        // Adjust movement based on tilt
        if(accelX < 8) {
            if(accelX < 8 && accelX > 6)
                player.velocity.setLength(3);
            else if(accelX < 6 && accelX > 4)
                player.velocity.setLength(4);
            else if(accelX < 4)
                player.velocity.setLength(5);
        }
        else{
            player.velocity.setLength(0);
        }
        player.update();

        // update shots
        for(int i = 0; i < shotList.size(); i++) {
            Sprite shot = shotList.get(i);
            shot.update();

            if(shot.offScreen){
                shotList.remove(i);
            }
        }

        double xSpawn, ySpawn;

        // Checking for collision between rock and shot
        for(int i = 0; i < shotList.size(); i++){
            Sprite shot = shotList.get(i);
            for(int j = 0; j < rockList.size(); j++){
                Sprite rock = rockList.get(j);
                if(shot.collision(rock)){
                    // Get position of collided rock to spawn children
                    xSpawn = rock.position.x;
                    ySpawn = rock.position.x;
                    shotList.remove(i);
                    // add points to player based on rock level destroyed
                    player.addPoints((int)(10*rock.level));

                    // check the rock level and spawn the appropriate child rock(s)
                    if(rock.level == 1) {
                        for(int n = 0; n < 2; n++) {
                            this.createRock(xSpawn, ySpawn, 5,
                                    150, 150, 2);
                        }
                    }
                    else if(rock.level == 2){
                        for(int n = 0; n < 3; n++) {
                            this.createRock(xSpawn, ySpawn, 6,
                                    100, 100, 3);
                        }
                    }
                    else if(rock.level == 3){
                        for(int n = 0; n < 4; n++) {
                            this.createRock(xSpawn, ySpawn, 7,
                                    75, 75, 4);
                        }
                    }
                    rockList.remove(j);
                }
            }
        }

        // if all rocks get removed add a new one
        if(rockList.isEmpty()){
            for(int i = 0; i < maxRocks; i++)
                this.createRock(this.generateSpawnX(), this.generateSpawnY(), 5, 200, 200,1);
        }

        // Levels, maximum of 5. Each level increases the maximum amount of level 1 rocks
        if(player.points > 500)
            maxRocks = 2;
        if(player.points > 1000)
            maxRocks = 3;
        if(player.points > 2500)
            maxRocks = 4;
        if(player.points > 5000)
            maxRocks = 5;

        // Checking for collision between player and rock
        for(int j = 0; j < rockList.size(); j++){
            Sprite rock = rockList.get(j);
            if(player.collision(rock)){
                rockList.remove(j);
                lives--;
                if(lives == 0 && !MainMenuActivity.practiceModeEnabled)
                    this.playerKilled();

                // if all rocks get removed add a new one
                if(rockList.size() == 0){
                    this.createRock(this.generateSpawnX(), this.generateSpawnY(), 5, 200, 200,1);
                }
            }
        }

        // Update all rocks
        for(Sprite rock : rockList)
            rock.update();
    }

    //This method is called when the screen is touched
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            double clickX = event.getX();
            double clickY = event.getY();

            // Used to check if the pause button is clicked
            if(clickX > screenWidth-100 && clickY < 100){
                gamePaused();
                return true;
            }
            // Everytime the screen is touched a shot is created
            // Shots exist until they are completely off the screen.
            Sprite shot = new Sprite(BitmapFactory.decodeResource(getResources(),R.drawable.laser));
            shot.setScreenSize(screenWidth, screenHeight);
            shot.position.set(player.position.x + (player.image.getWidth()/2) - (shot.image.getHeight()/2),
                    player.position.y + (player.image.getHeight()/2) - (shot.image.getWidth()/2));
            shot.velocity.setLength(20);
            shot.velocity.setAngle(player.rotation);
            shot.rotation = player.rotation - 90;
            shotList.add(shot);

            return true;
        }
        return false;
    }

    //The canvas is redrawn each time. To delete things just dont draw them and move them off screen
    //Attempt to reuse old deleted objects
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // Draw background
        //canvas.drawColor(Color.WHITE);
        background.draw(canvas);
        // Draw all shots
        for(Sprite shot : shotList)
            shot.draw(canvas);
        // Draw player
        player.draw(canvas);
        // Draw all rocks
        for(Sprite rock : rockList)
            rock.draw(canvas);
        // Draw score and level
        canvas.drawText("Score: " + player.points, 20, 40, textPaint);
        canvas.drawText("Level: " + maxRocks, 20, 80, textPaint);
        canvas.drawText("Lives: " + lives, 20, 120, textPaint);
        // Draw pause sprite
        pause.draw(canvas);
    }

    // Creates a rock and adds it to the rockList
    private void createRock(double posX, double posY, double velocity, int scaleWidth, int scaleHeight, int level){
        Sprite rock = new Sprite(BitmapFactory.decodeResource(getResources(),R.drawable.asteroid));
        rock.setScreenSize(screenWidth, screenHeight);
        rock.position.set(posX, posY);
        rock.wrapOn = true;
        rock.velocity.setLength(Math.random()*velocity);
        rock.velocity.setAngle(Math.random()*360);
        rock.setImage(Bitmap.createScaledBitmap(rock.image, scaleWidth, scaleHeight, false));
        rock.setLevel(level);
        rockList.add(rock);
    }

    // Randomly selects the right or left hand side of the screen
    private double generateSpawnX(){
        boolean side = Math.random() < 0.5;
        if(side)
            return 100;
        else
            return screenWidth-100;
    }

    // Randomly selects the top or bottom of the screen
    private double generateSpawnY(){
        boolean side = Math.random() < 0.5;
        if(side)
            return 100;
        else
            return screenHeight-100;
    }

    // Used to launch the game over activity and end the thread
    private void playerKilled() throws InterruptedException {
        // Stop game thread
        thread.setRunning(false);

        Executors.newSingleThreadExecutor().execute(() -> {
            Intent intent = new Intent(parentContext, GameOverActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("gamescore",player.points);
            parentContext.startActivity(intent);
        });
    }

    // Used to launch the pause activity
    private void gamePaused(){
        // Pause the thread
        thread.setRunning(false);

        // Inflate pause menu fragment
        replaceFragment(PauseMenuFragment.newInstance(this));
    }

    public void gameResume() {
        Log.d(TAG, "gameResume() called!!!");
        // Remove pause menu fragment
        removeFragment(fragmentManager.findFragmentByTag(FRAGMENT_TAG));

        // Resume game thread
        Executors.newSingleThreadExecutor().execute(() -> {
            thread.setRunning(true);
            thread.run();
            setFocusable(true);
        });
    }

    public void setFragmentManager(FragmentManager fragManager) {
        fragmentManager = fragManager;
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.game_frame, fragment, FRAGMENT_TAG);
        fragmentTransaction.commit();
    }

    public void removeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }
}
