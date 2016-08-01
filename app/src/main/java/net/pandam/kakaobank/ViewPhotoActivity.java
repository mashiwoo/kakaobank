package net.pandam.kakaobank;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.androidquery.AQuery;

import java.io.File;

public class ViewPhotoActivity extends AppCompatActivity {

    private AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        initialize();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void initialize() {
        aq = new AQuery(this);

        String fileName = getIntent().getStringExtra("fileName");
        File fileImage = new File(Environment.getExternalStorageDirectory() + "/kakaobank/" + fileName);

        aq.id(R.id.ivPhoto).image(fileImage, ActionBar.LayoutParams.WRAP_CONTENT);

    }
}
