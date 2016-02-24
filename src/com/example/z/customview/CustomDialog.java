package com.example.z.customview;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.z.R;
import com.example.z.app.App;
import com.example.z.customview.shadowviewhelper.ShadowProperty;
import com.example.z.customview.shadowviewhelper.ShadowViewHelper;

public class CustomDialog {

	public static Dialog confirm(String contentText, String confirmText, String cancelText,
			final OnConfirmListener confirmListener, final OnCancelsListener cancelListener) {
		final Dialog dialog = new Dialog(App.getCurrentActivity(), R.style.MyDialog);
		View layout = LayoutInflater.from(App.getCurrentActivity()).inflate(R.layout.view_dialog_cc, null);
		ShadowViewHelper.bindShadowHelper(new ShadowProperty().setShadowColor(0x2b000000).setShadowRadius(15), layout, 0, 0);
		TextView content = (TextView) layout.findViewById(R.id.content);
		TextView confirm = (TextView) layout.findViewById(R.id.confirm);
		TextView cancel = (TextView) layout.findViewById(R.id.cancel);
		content.setText(contentText);
		confirm.setText(confirmText);
		cancel.setText(cancelText);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (confirmListener != null) {
					confirmListener.onConfirmListener();
				}
				dialog.dismiss();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cancelListener != null) {
					cancelListener.onCancelListener();
				}
				dialog.dismiss();
			}
		});
		dialog.setContentView(layout);
		dialog.show();

		return dialog;
	}

	public static Dialog alert(String contentText, String confirmText, final OnConfirmListener confirmListener) {
		final Dialog dialog = new Dialog(App.getCurrentActivity(), R.style.MyDialog);
		View layout = LayoutInflater.from(App.getCurrentActivity()).inflate(R.layout.view_dialog_c, null);
		ShadowViewHelper.bindShadowHelper(new ShadowProperty().setShadowColor(0x2b000000).setShadowRadius(15), layout);
		TextView content = (TextView) layout.findViewById(R.id.content);
		TextView confirm = (TextView) layout.findViewById(R.id.confirm);
		content.setText(contentText);
		confirm.setText(confirmText);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (confirmListener != null) {
					confirmListener.onConfirmListener();
				}
				dialog.dismiss();
			}
		});

		dialog.setContentView(layout);
		dialog.show();

		return dialog;
	}

	public static Dialog excetion(final OnConfirmListener confirmListener) {
		final Dialog dialog = new Dialog(App.getCurrentActivity(), R.style.MyDialog);
		View layout = LayoutInflater.from(App.getCurrentActivity()).inflate(R.layout.view_dialog_exception, null);
		ShadowViewHelper.bindShadowHelper(new ShadowProperty().setShadowColor(0x2b000000).setShadowRadius(15), layout);
		TextView confirm = (TextView) layout.findViewById(R.id.confirm);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (confirmListener != null) {
					confirmListener.onConfirmListener();
				}
				dialog.dismiss();
			}
		});

		dialog.setContentView(layout);
		dialog.show();

		return dialog;
	}

	public static Dialog heartProgressBar() {
		final Dialog dialog = new Dialog(App.getCurrentActivity(), R.style.MyDialog);
		View layout = LayoutInflater.from(App.getCurrentActivity()).inflate(R.layout.dialog_custom_loading, null);
		ShadowViewHelper.bindShadowHelper(new ShadowProperty().setShadowColor(0x69f1da84).setShadowRadius(50), layout,
				0xfff1da84, 20, 20);
		final HeartProgressBar heartProgressBar = (HeartProgressBar) layout.findViewById(R.id.progressBar);

		dialog.setContentView(layout);
		dialog.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface arg0) {
				heartProgressBar.start();
			}
		});

		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				heartProgressBar.dismiss();
			}
		});
		dialog.show();

		return dialog;
	}

	public interface OnConfirmListener {
		void onConfirmListener();
	}

	public interface OnCancelsListener {
		void onCancelListener();
	}
}
