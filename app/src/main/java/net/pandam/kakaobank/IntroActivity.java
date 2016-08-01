package net.pandam.kakaobank;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.androidquery.AQuery;

public class IntroActivity extends Activity {

    private AQuery aq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        initialize();

    }

    private void initialize()
    {
        aq = new AQuery(this);

        aq.id(R.id.ivIntro).animate(R.anim.action_intro);
        MainThreadEnd();
    }


    private void MainThreadEnd()
    {
        Handler handler =    new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                Thread splashThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            int waited = 0;
                            while (waited < 3000) {

                                sleep(200);
                                waited += 500;
                            }
                        } catch (InterruptedException e) {
                            // do nothing
                        } finally {
                            finish();
                        }
                    }
                };
                splashThread.start();
                finish();    // 액티비티 종료
            }
        };

        handler.sendEmptyMessageDelayed(0, 3000);    // ms, 3초후 종료시킴
    }

}
