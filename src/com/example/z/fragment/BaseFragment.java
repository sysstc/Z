package com.example.z.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.z.activity.BaseActivity;
import com.example.z.activity.MainActivity;

public class BaseFragment extends Fragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public BaseActivity getBaseActivity() {
		return (BaseActivity) getActivity();
	}

	public MainActivity getMainActivity() {
		Activity activity = getActivity();
		if (activity != null) {
			if (activity instanceof MainActivity) {
				return ((MainActivity) activity);
			}
		}
		return null;
	}
}
