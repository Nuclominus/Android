package com.nuclominus.offlinetwitterclient.Utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

public class AnimateUtils {

    public static boolean animProgress = false;

    public static void expand(View view, final View hide) {

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec, heightSpec);

        final float size_menu = hide.getHeight()/4;

        ValueAnimator mAnimator = slideAnimator(1, (int)size_menu, view);

        mAnimator.addListener(animator);

        mAnimator.start();
    }

    public static void collapse(final View v, final View hide) {
        int finalHeight = v.getHeight();
        ValueAnimator mAnimator = slideAnimator(finalHeight, 1, v);

        mAnimator.addListener(animator);
        mAnimator.start();
    }

    private static Animator.AnimatorListener animator = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {
            animProgress = true;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            animProgress = false;
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };


    public static ValueAnimator slideAnimator(int start, int end, final View v) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }
}
