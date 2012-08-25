package com.jim.tvs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.jim.common.Utils;
import com.jim.data.DataProviderManager;
import com.jim.data.StreamInfo;

public class StreamListAdapter extends BaseAdapter implements OnItemClickListener, OnItemLongClickListener {
	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private HashMap<String, Object> mData = null;
	
	private ArrayList<String> mTitleList = null;
	
	private DataProviderManager mDataManager = DataProviderManager.getInstance();
	
	private EditText mEtTypeName = null;
	
	private int mLongClickPosition = 0;
	
	public StreamListAdapter(Context context, LayoutInflater inflater) {
		mContext = context;
		mInflater = inflater;
	}

	@Override
	public int getCount() {
		return mTitleList == null ? 0 : mTitleList.size();
	}

	@Override
	public Object getItem(int position) {
		return mTitleList == null ? null : mTitleList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_layout, null);
			
			holder = new Holder();
			holder.tv_tips = (TextView) convertView.findViewById(R.id.tv_tips);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
//		holder.tv_tips.setText(PinyinUtil.stringToPinyin(mTitleList.get(position))[0]);
		holder.tv_title.setText(mTitleList.get(position));
		
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long arg3) {
		try {
			Uri uri = Uri.parse((String) mData.get(mTitleList.get(position)));
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.setType("video/*");
			intent.setDataAndType(uri, "video/*");
			mContext.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(mContext, mContext.getString(R.string.no_software_to_play), Toast.LENGTH_SHORT).show();
		}
		
	}
	
	public void setList(ArrayList<StreamInfo> list) {
		mData = new HashMap<String, Object>();
		mTitleList = new ArrayList<String>();
		for(int i = 0 ; i < list.size(); i++) {
			mData.put(list.get(i).getTitle(), list.get(i).getUrl());
			mTitleList.add(list.get(i).getTitle());
		}
		
		mTitleList = Utils.getOrderStreamTitle(mTitleList);
		
		notifyDataSetChanged();
	}
	
	class Holder {
		TextView tv_tips = null;
		TextView tv_title = null;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
			long arg3) {
		mLongClickPosition = position;
		showListDialog();
		return false;
	}
	
	private void showListDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(mContext.getString(R.string.choose_belong));
		
//		ArrayList<CustomTypeInfo> customTypeList = mDataManager.getAllCustomType();
		
		final List<String> typeTabNameList = mDataManager.getAllTypeName();
		typeTabNameList.remove(0);
		typeTabNameList.add(mContext.getString(R.string.add_custom_type));
		String[] typeName = (String[]) typeTabNameList.toArray(new String[typeTabNameList.size()]);
		
		builder.setItems(typeName, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which == typeTabNameList.size() - 1) {
					showNewListEditDialog();
					try {
						FlurryAgent.logEvent("New_Type");
					} catch(Exception e) {
						e.printStackTrace();
					}
					
				} else {
					try {
						FlurryAgent.logEvent("Add_To_Type");
					} catch(Exception e) {
						e.printStackTrace();
					}
					
					StreamInfo info = new StreamInfo();
					String title = mTitleList.get(mLongClickPosition);
					info.setTitle(title);
					info.setUrl((String) mData.get(title));
					if(addItemToPersonalList(info, typeTabNameList.get(which))) {
						Utils.makeText(R.string.added_successful);
					} else {
						Utils.makeText(R.string.added_fail);
					}
				}
			}
		});
		
		builder.create().show();
	}
	
	private void showNewListEditDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(mContext.getString(R.string.new_type_edit_title));
		
		View customView = mInflater.inflate(R.layout.dialog_edit_view, null);
		mEtTypeName = (EditText) customView.findViewById(R.id.et_type_name);
		
		builder.setView(customView);
		
		builder.setPositiveButton(R.string.dialog_ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(Utils.isTypeNameExists(mEtTypeName.getText().toString())) {
					Toast.makeText(mContext, mContext.getString(R.string.name_existed), Toast.LENGTH_SHORT).show();
				} else {
					StreamInfo info = new StreamInfo();
					String title = mTitleList.get(mLongClickPosition);
					info.setTitle(title);
					info.setUrl((String) mData.get(title));
					if(addItemToPersonalList(info,mEtTypeName.getText().toString())) {
						Utils.makeText(R.string.added_successful);
					} else {
						Utils.makeText(R.string.added_fail);
					}
				}
			}
		});
		builder.setNegativeButton(R.string.dialog_cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		
		builder.create().show();
	}
	
	private boolean addItemToPersonalList(StreamInfo info, String typeName) {
		return mDataManager.addItemToPersonalList(info, typeName);
	}
	
}