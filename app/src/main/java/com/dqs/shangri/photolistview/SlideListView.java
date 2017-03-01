package com.dqs.shangri.photolistview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.logging.Logger;

/**
 * Created by Shangri on 2017/3/1.
 */

public class SlideListView {
    public SlideListView(Context context, ListView listView, View view) {
        this.context = context;
        this.listView = listView;
        this.view = view;
    }

    private Logger log = Logger.getLogger(getClass().getName());
    private Context context;
    private ListView listView;
    private View view;
    private int slideTime = 100;
    private GestureDetector mGestureDetector;
    private int pos = -1;
    private volatile ImageState imageState = ImageState.ORI;
    private int firstVisiblePos = -1;

    public void setSclideTime(int slideTime) {
        this.slideTime = slideTime;
    }

    public void init() {
        mGestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
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
                if (e2.getY() < dip2px(context, 2)) {
                    hideView();
                    return true;
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
        mGestureDetector.setIsLongpressEnabled(false);

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态
                        log.info("空闲状态");
                        if (imageState == ImageState.MOVE && firstVisiblePos == 0) {
                            log.info("showView");
                            showView();
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING://滚动状态
                        log.info("滚动状态");
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://触摸后滚动
                        log.info("触摸后滚动");
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //log.info("firstVisibleItem: " + firstVisibleItem + " visibleItemCount: " + visibleItemCount + " totalItemCount: " + totalItemCount);
                firstVisiblePos = firstVisibleItem;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (imageState == ImageState.MOVE) {
                    pos = position;
                    log.info("onItemClick, pos: " + position);
                    showView();
                }
            }
        });

    }

    private void showView() {
        if (imageState != ImageState.MOVE) {
            return;
        }
        imageState = ImageState.SLIDING;
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "Y", -dip2px(context, 200), 0.0f);
        animator.setDuration(slideTime);
        animatorSet.play(animator);
        animatorSet.setInterpolator(new MyLinearInterpolator(listView, dip2px(context, 200), false));
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (pos != -1) {
                    listView.requestFocus();
                    listView.setItemChecked(pos, true);
                    listView.setSelection(pos);
                    listView.smoothScrollToPosition(pos);
                    pos = -1;
                }
                imageState = ImageState.ORI;
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

    private Handler handler = new Handler();

    private void hideView() {
        if (imageState != ImageState.ORI) {
            return;
        }
        imageState = ImageState.SLIDING;
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "Y", 0.0f, -dip2px(context, 200));
        animator.setDuration(slideTime);

        animatorSet.play(animator);

        animatorSet.setInterpolator(new MyLinearInterpolator(listView, dip2px(context, 200), true));

        animatorSet.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                imageState = ImageState.MOVE;
                                            }
                                        }, 200);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                }

        );


        animatorSet.start();

        log.info("hideImageView");
    }

    private enum ImageState {
        ORI,
        SLIDING,
        MOVE
    }

    private class MyLinearInterpolator extends LinearInterpolator {
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
            view.setLayoutParams(layoutParams);
            view.requestLayout();
            return super.getInterpolation(input);
        }
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
