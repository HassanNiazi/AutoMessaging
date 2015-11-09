package nexgen.automessaging;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

/**
 * Created by Hassan Niazi on 10/3/2015.
 */
public class Splash extends Activity{

    MediaPlayer splashsound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        splashsound = MediaPlayer.create(Splash.this,R.raw.splashsound);
        splashsound.start();
        Thread timer = new Thread(){
            public void run(){
                try
                {
                    sleep(3000);
                }
                catch (InterruptedException e)
                {
                 e.printStackTrace();
                }
                finally {
                    Intent openMainActivity = new Intent("nexgen.automessaging.MENU");
                    startActivity(openMainActivity);
                }
            }
        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        splashsound.release();
        finish();
    }
}
