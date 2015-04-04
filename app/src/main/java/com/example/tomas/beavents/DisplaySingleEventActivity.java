package com.example.tomas.beavents;

import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by LLP-student on 4/3/2015.
 */
public class DisplaySingleEventActivity extends BaseActivity {

    private Event mEvent;
    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_single_event_activity);
        super.onCreateDrawer();

        this.mEvent = (Event) getIntent().getSerializableExtra(MainActivity.SER_KEY);

        this.mTextView = (TextView) findViewById(R.id.eventText);

        mTextView.setText(mEvent.getImage());




    }


}

