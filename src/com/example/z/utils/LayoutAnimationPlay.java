package com.example.z.utils;

import java.util.ArrayList;
import java.util.List;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class LayoutAnimationPlay {

	private List<ObjectAnimator> listOA = new ArrayList<ObjectAnimator>();
	private List<ObjectAnimator> eListOA = new ArrayList<ObjectAnimator>();
	private int duration = 0;
	private long startDelay = 0;
	private Interpolator interpolator = new DecelerateInterpolator();

	public LayoutAnimationPlay(ABLayoutAnimation a) {
		listOA = a.listOA;
		eListOA = a.eListOA;
		duration = a.duration;
		startDelay = a.startDelay;
		interpolator = a.interpolator;

		AnimatorSet set = new AnimatorSet();
		for (int j = 0; j < listOA.size(); j++) {
			set.playTogether(listOA.get(j));
		}
		set.setInterpolator(interpolator);
		set.setStartDelay(0);
		set.setDuration(0);
		set.start();

		AnimatorSet eset = new AnimatorSet();
		for (int j = 0; j < eListOA.size(); j++) {
			eset.playTogether(eListOA.get(j));
		}
		eset.setInterpolator(interpolator);
		eset.setStartDelay(startDelay);
		eset.setDuration(duration);
		eset.start();
	}

	public static ABLayoutAnimation from(Object t) {
		return new ABLayoutAnimation(t);
	}

	public static class ABLayoutAnimation {

		private Object target;
		private List<ObjectAnimator> listOA = new ArrayList<ObjectAnimator>();
		private List<ObjectAnimator> eListOA = new ArrayList<ObjectAnimator>();
		private int duration = 0;
		private long startDelay = 0;
		private Interpolator interpolator = new DecelerateInterpolator();

		public ABLayoutAnimation(Object t) {
			target = t;
		}

		public ABLayoutAnimation translationX(float... values) {
			listOA.add(ObjectAnimator.ofFloat(target, "translationX", values));
			eListOA.add(ObjectAnimator.ofFloat(target, "translationX", 0));
			return this;
		}

		public ABLayoutAnimation translationY(float... values) {
			listOA.add(ObjectAnimator.ofFloat(target, "translationY", values));
			eListOA.add(ObjectAnimator.ofFloat(target, "translationY", 0));
			return this;
		}

		public ABLayoutAnimation alpha(float... values) {
			listOA.add(ObjectAnimator.ofFloat(target, "alpha", values));
			eListOA.add(ObjectAnimator.ofFloat(target, "alpha", 1));
			return this;
		}

		public ABLayoutAnimation rotation(float... values) {
			listOA.add(ObjectAnimator.ofFloat(target, "rotation", values));
			eListOA.add(ObjectAnimator.ofFloat(target, "rotation", 0));
			return this;
		}

		public ABLayoutAnimation rotationY(float... values) {
			listOA.add(ObjectAnimator.ofFloat(target, "rotationY", values));
			eListOA.add(ObjectAnimator.ofFloat(target, "rotationY", 0));
			return this;
		}

		public ABLayoutAnimation rotationX(float... values) {
			listOA.add(ObjectAnimator.ofFloat(target, "rotationX", values));
			eListOA.add(ObjectAnimator.ofFloat(target, "rotationX", 0));
			return this;
		}

		public ABLayoutAnimation scaleX(float... values) {
			listOA.add(ObjectAnimator.ofFloat(target, "scaleX", values));
			eListOA.add(ObjectAnimator.ofFloat(target, "scaleX", 0));
			return this;
		}

		public ABLayoutAnimation scaleY(float... values) {
			listOA.add(ObjectAnimator.ofFloat(target, "scaleY", values));
			eListOA.add(ObjectAnimator.ofFloat(target, "scaleY", 0));
			return this;
		}

		public ABLayoutAnimation duration(int t) {
			duration = t;
			return this;
		}

		public ABLayoutAnimation startDelay(int t) {
			startDelay = t;
			return this;
		}

		public ABLayoutAnimation interpolator(Interpolator i) {
			interpolator = i;
			return this;
		}

		public LayoutAnimationPlay start() {
			return new LayoutAnimationPlay(this);
		}

	}

}
