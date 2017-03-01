package com.dqs.shangri.photolistview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    private List<ListInfo> listInfos = new ArrayList<>();
    private Logger log = Logger.getLogger(getClass().getName());
    private ListView listView;
    private ListApdater listApdater;
    private ImageView imageView;
    private SlideListView slideListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list_view);
        imageView = (ImageView) findViewById(R.id.image_view);


        listApdater = new ListApdater(listInfos, this);
        for (int i = 0; i < 100; i++) {
            listInfos.add(new ListInfo(i + ""));
        }
        listView.setAdapter(listApdater);

        slideListView = new SlideListView(this, listView, imageView);
        slideListView.setSlidingDistance(200);
        slideListView.init();
    }
}
