package com.dqs.shangri.photolistview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    private List<ListInfo> listInfos = new ArrayList<>();
    private Logger log = Logger.getLogger(getClass().getName());
    private ListView listView;
    private ListApdater listApdater;
    private GestureDetector mGestureDetector;
    private ImageView imageView;
    private final int slideTime = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list_view);
        imageView = (ImageView) findViewById(R.id.image_view);

        mGestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                log.info("onScroll e1 y: " + e1.getY() + " x: " + e1.getX());
                log.info("onScroll e2 y: " + e2.getY() + " x: " + e2.getX());
                if (e2.getY() < 0) {
                    log.info("onScroll image");
                    hideImageView();
                    return true;
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                log.info("onFling e1 y: " + e1.getY() + " x: " + e1.getX());
                log.info("onFling e2 y: " + e2.getY() + " x: " + e2.getX());
                return false;
            }
        });
        mGestureDetector.setIsLongpressEnabled(false);

        listApdater = new ListApdater(listInfos, this);
        for (int i = 0; i < 100; i++) {
            listInfos.add(new ListInfo(i+""));
        }
        listView.setAdapter(listApdater);

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                /*log.info("visibleItemCount: " + visibleItemCount);
                log.info("firstVisibleItem: " + firstVisibleItem);
                log.info("totalItemCount: " + totalItemCount);*/
                if (imageState == ImageState.MOVE && firstVisibleItem == 0) {
                    showImageView();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (imageState == ImageState.MOVE) {
                    pos = position;
                    showImageView();
                }
            }
        });
    }

    private int pos = -1;

    private ImageState imageState = ImageState.ORI;

    private enum ImageState {
        ORI,
        TRANING,
        MOVE
    }

    private void hideImageView() {
        if (imageState != ImageState.ORI) {
            return;
        }
        imageState = ImageState.TRANING;
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "Y", 0.0f, -dip2px(this, 200));
        animator.setDuration(slideTime);

        animatorSet.play(animator);

        animatorSet.setInterpolator(new MyLinearInterpolator(listView, dip2px(this, 200), true));

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                imageState = ImageState.MOVE;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        animatorSet.start();

        log.info("hideImageView");
    }

    private void showImageView() {
        if (imageState != ImageState.MOVE) {
            return;
        }
        imageState = ImageState.TRANING;
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "Y", -dip2px(this, 200), 0.0f);
        animator.setDuration(slideTime);
        animatorSet.play(animator);
        animatorSet.setInterpolator(new MyLinearInterpolator(listView, dip2px(this, 200), false));
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                imageState = ImageState.ORI;
                if (pos != -1) {
                    log.info("pos: " + pos + " id: " + Thread.currentThread().getId());
                    listView.requestFocus();
                    listView.setItemChecked(pos, true);
                    listView.setSelection(pos);
                    listView.smoothScrollToPosition(pos);
                    pos = -1;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animatorSet.start();
        log.info("showImageView");
    }

    class MyLinearInterpolator extends LinearInterpolator {
        private View view;
        private int marginTop;
        private boolean direction;

        public MyLinearInterpolator(View view, int marginTop, boolean direction) {
            this.view = view;
            this.marginTop = marginTop;
            this.direction = direction;
        }

        @Override
        public float getInterpolation(float input) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
            if (direction) {
                layoutParams.topMargin = (int) (marginTop * (1 - input));
            } else {
                layoutParams.topMargin = (int) (marginTop * (input));
            }
            log.info("input: " + input + " topMargin: " + layoutParams.topMargin + " id: " + Thread.currentThread().getId());
            view.setLayoutParams(layoutParams);
            view.requestLayout();
            return super.getInterpolation(input);
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
