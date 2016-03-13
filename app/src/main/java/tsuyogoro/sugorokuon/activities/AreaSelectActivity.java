package tsuyogoro.sugorokuon.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import tsuyogoro.sugorokuon.R;

public class AreaSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_area_select);

        Toolbar toolbar = (Toolbar) findViewById(R.id.area_select_activity_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }
}
