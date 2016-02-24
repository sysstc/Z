package com.example.z.utils;

import android.graphics.drawable.Drawable;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.z.http.ABImageLoadingCallBack;
import com.example.z.http.ImageLoading;

public class ABViewBinder {

	public static void setTextView(TextView textView, Spanned content) {
		textView.setText(!TextUtils.isEmpty(content) ? content : "");
	}

	public static void setTextView(TextView textView, String content) {
		setTextView(textView, content, "");
	}

	public static void setTextView(TextView textView, String content, String emptyTip) {
		textView.setText(!TextUtils.isEmpty(content) ? content : emptyTip);
	}

	public static void setEditText(EditText edittext, String content) {
		setEditText(edittext, content, "");
	}

	public static void setEditText(EditText edittext, String content, String emptyTip) {
		edittext.setText(!TextUtils.isEmpty(content) ? content : emptyTip);
	}

	public static void setButtonText(Button button, String text) {
		setButtonText(button, text, "");
	}

	public static void setButtonText(Button button, String text, String emptytext) {
		button.setText(!TextUtils.isEmpty(text) ? text : emptytext);
	}

	public static void setRatingBar(RatingBar ratingBar, float rating) {
		ratingBar.setRating(rating);
	}

	public static void setProgressBar(ProgressBar pb, int progress) {
		pb.setProgress(progress);
	}

	public static void setProgressDrawable(ProgressBar pb, Drawable drawable) {
		pb.setProgressDrawable(drawable);
	}

	public static void setImageView(ImageView imageView, String uri) {
		ImageLoading.setImageView(imageView, uri);
	}

	public static void setImageView(ImageView imageView, String uri, final ABImageLoadingCallBack callback) {
		ImageLoading.setImageView(imageView, uri, callback);
	}

}
