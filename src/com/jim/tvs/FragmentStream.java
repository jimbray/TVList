package com.jim.tvs;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.flurry.android.FlurryAgent;
import com.jim.common.Constants;
import com.jim.data.GetStreamData;
import com.jim.data.StreamInfo;
import com.jim.myinterface.InterruptTask;
import com.jim.myinterface.RequestListener;

public class FragmentStream extends SherlockFragment implements RequestListener {
	private String mPath = null;
	
	private LinearLayout mLayoutLoading = null;
	private ProgressBar mPgbLoading = null;
	private TextView mTvLoading = null;
	
	private ListView mListView = null;
	private BaseAdapter mListAdapter = null;
	
	private InterruptTask mCurTask = null;
	
	private ArrayList<StreamInfo> mCurList = null;
	
	public static Fragment newInstance() {
		Fragment fg = new FragmentStream();
		return fg;
	}

	@Override
	public void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(getSherlockActivity(), Constants.FLURRY_UA_NUM);
		try {
			FlurryAgent.logEvent("Stream_View");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPath = getString(R.string.path);
		
		if(savedInstanceState != null) {
			mCurList = savedInstanceState.getParcelableArrayList("list");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_view_layout, container, false);
		
		mLayoutLoading = (LinearLayout) view.findViewById(R.id.laoding_layout);
		mPgbLoading = (ProgressBar) view.findViewById(R.id.pgb_loading);
		mTvLoading = (TextView) view.findViewById(R.id.tv_loading);
		mTvLoading.setEnabled(false);
	    mTvLoading.setOnClickListener(mLoadAgainListener);
		
		mListView = (ListView) view.findViewById(R.id.list_view);
		mListAdapter = new StreamListAdapter(getSherlockActivity(), inflater);
		mListView.setOnItemClickListener((OnItemClickListener) mListAdapter);
		mListView.setOnItemLongClickListener((OnItemLongClickListener) mListAdapter);
		mListView.setAdapter(mListAdapter);
		
		getData();
		return view;
	}
	
	private void getData() {
		if(mCurList == null) {
			mCurTask = new GetStreamData(mPath, this);
			((GetStreamData)mCurTask).execute();
		} else {
			mLayoutLoading.setVisibility(View.GONE);
			((StreamListAdapter)mListAdapter).setList(mCurList);
		}
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("list", mCurList);
	}

	private OnClickListener mLoadAgainListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mTvLoading.setEnabled(false);
			getData();
		}
	};

	@Override
	public void OnGetDataBegin() {
		if(mLayoutLoading != null) {
			mLayoutLoading.setVisibility(View.VISIBLE);
			mPgbLoading.setVisibility(View.VISIBLE);
			mTvLoading.setText(getString(R.string.loading_text));
			mTvLoading.setEnabled(false);
		}
	}

	@Override
	public void OnGetDataChange() {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void OnGetDataComplete(Object response) {
		if(response != null) {
			mLayoutLoading.setVisibility(View.GONE);
			mCurList = (ArrayList<StreamInfo>)response;
			((StreamListAdapter)mListAdapter).setList((ArrayList<StreamInfo>)response);
		} else {
			mLayoutLoading.setVisibility(View.VISIBLE);
			mPgbLoading.setVisibility(View.GONE);
			mTvLoading.setText(getString(R.string.load_no_data));
			mTvLoading.setEnabled(false);
		}
	}

	@Override
	public void OnGetDataException(Exception e) {
		getSherlockActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(mLayoutLoading != null) {
					mLayoutLoading.setVisibility(View.VISIBLE);
					mPgbLoading.setVisibility(View.GONE);
					mTvLoading.setText(getString(R.string.load_error));
					mTvLoading.setEnabled(true);
				}
			}
		});
	}
	
	@Override
	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(getSherlockActivity());
		interruptTask();
	}

	private void interruptTask() {
		if(mCurTask != null) {
			mCurTask.interrupt();
		}
	}
}
