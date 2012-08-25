package com.jim.tvs;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.flurry.android.FlurryAgent;
import com.jim.common.Constants;
import com.jim.common.Utils;
import com.jim.data.DataProviderManager;
import com.jim.data.StreamInfo;

public class FragmentPersonal extends SherlockFragment {
	private LinearLayout mLayoutLoading = null;
	private ProgressBar mPgbLoading = null;
	private TextView mTvLoading = null;
	
	private ExpandableListView mEpListView = null;
	private ExpandableListAdapter mListAdapter = null;
	
	private List<String> mGroupData = null;
	private List<List<StreamInfo>> mChildData = null;
	
	private DataProviderManager mDataManager = DataProviderManager.getInstance();
	
	public static Fragment newInstance() {
		Fragment fg = new FragmentPersonal();
		return fg;
	}

	@Override
	public void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(getSherlockActivity(), Constants.FLURRY_UA_NUM);
		try {
			FlurryAgent.logEvent("Personal_View");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(getSherlockActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.personal_layout, container, false);
		
		mLayoutLoading = (LinearLayout) view.findViewById(R.id.laoding_layout);
		mPgbLoading = (ProgressBar) view.findViewById(R.id.pgb_loading);
		mTvLoading = (TextView) view.findViewById(R.id.tv_loading);
		mEpListView = (ExpandableListView) view.findViewById(R.id.eplv);
		mEpListView.setGroupIndicator(null);
		
		mLayoutLoading.setVisibility(View.VISIBLE);
		mPgbLoading.setVisibility(View.VISIBLE);
		mTvLoading.setVisibility(View.VISIBLE);
		mTvLoading.setText(R.string.loading_text);
		
		mListAdapter = new MyExpandableListAdapter(getSherlockActivity(), this, LayoutInflater.from(getSherlockActivity()), null, null);
		mEpListView.setAdapter(mListAdapter);
		mEpListView.setOnChildClickListener((OnChildClickListener) mListAdapter);
		mEpListView.setOnItemLongClickListener((OnItemLongClickListener) mListAdapter);
		
		updateData();
		
		return view;
	}

	public void updateData() {
		mGroupData = mDataManager.getAllTypeName();
		if(mGroupData.size() > 1) {
			mLayoutLoading.setVisibility(View.GONE);
			
			if(mChildData == null) {
				mChildData = new ArrayList<List<StreamInfo>>();
			} else {
				mChildData.clear();
			}
			
			for(int i = 1; i < mGroupData.size(); i++) {
				List<StreamInfo> childList = mDataManager.getListByType(mGroupData.get(i));
								
				mChildData.add(childList);
			}
		} else {
			mLayoutLoading.setVisibility(View.VISIBLE);
			mPgbLoading.setVisibility(View.GONE);
			mTvLoading.setVisibility(View.VISIBLE);
			mTvLoading.setText(R.string.no_custom_list);
		}
		
		((MyExpandableListAdapter) mListAdapter).setData(mGroupData, mChildData);
	}
	
	private class MyExpandableListAdapter extends BaseExpandableListAdapter implements OnChildClickListener, OnItemLongClickListener {
		private Context mContext = null;
		private Fragment mFgPersonal = null;
		private LayoutInflater mInflater = null;
		private List<String> mGroupList = null;
		private List<List<StreamInfo>> mChildList = null;
		
		private int[] childOperateStrId = {R.string.child_setting_remove, R.string.child_setting_add, R.string.child_setting_change};
		
		private int[] group_setting_item_id = {R.string.group_setting_edit, R.string.group_setting_remove};
		
		public MyExpandableListAdapter(Context context, Fragment fg, LayoutInflater inflater, List<String> group, List<List<StreamInfo>> childList) {
			this.mContext = context;
			this.mFgPersonal = fg;
			this.mInflater = inflater;
			this.mGroupList = group;
			this.mChildList = childList;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return mChildList.get(groupPosition).get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ChildHolder holder = null;
			
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_child, null);
				holder = new ChildHolder();
				
				holder.tv_title = (TextView) convertView.findViewById(R.id.tv_child);
				
				convertView.setTag(holder);
			} else {
				holder = (ChildHolder) convertView.getTag();
			}
			holder.tv_title.setText(mChildList.get(groupPosition).get(childPosition).getTitle());
			
			convertView.setTag(R.string.expandable_view_group_tag, groupPosition);
			convertView.setTag(R.string.expandable_view_child_tag, childPosition);
			
			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mChildList == null ? 0 : mChildList.get(groupPosition).size();
		}
		
		@Override
		public Object getGroup(int groupPosition) {
			return mGroupList.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return mGroupList == null ? 0 : mGroupList.size() - 1;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GroupHolder holder = null;
			
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_group, null);
				holder = new GroupHolder();
				
				holder.tv_type = (TextView) convertView.findViewById(R.id.tv_group);
				holder.iv_setting = (ImageView) convertView.findViewById(R.id.iv_group_setting);
				
				convertView.setTag(holder);
			} else {
				holder = (GroupHolder) convertView.getTag();
			}
			holder.tv_type.setText(mGroupList.get(groupPosition + 1) + " (" + mChildData.get(groupPosition).size() + ")");
			
			holder.iv_setting.setTag(mGroupList.get(groupPosition + 1));
			holder.iv_setting.setOnClickListener(groupSettingListener);
			
			convertView.setTag(R.string.expandable_view_group_tag, groupPosition);
			convertView.setTag(R.string.expandable_view_child_tag, -1);
			
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
		private OnClickListener groupSettingListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ImageView iv = (ImageView) v;
				String groupTableName = (String) iv.getTag();
				showGroupSettingDialog(groupTableName);
			}
		}; 
		
		private void showGroupSettingDialog(final String groupName) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(R.string.group_setting_dialog_title);
			
			String[] itemStr = {mContext.getString(group_setting_item_id[0]),mContext.getString(group_setting_item_id[1])};
			
			builder.setItems(itemStr, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch(which) {
					case 0:
						showGroupEditDialog(groupName);
						break;
						
					case 1:
						if(mDataManager.deleteType(groupName)) {
							((FragmentPersonal)mFgPersonal).updateData();
						} else {
							Utils.makeText(R.string.remove_fail);
						}
						break;
					}
				}
			});
			
			builder.create().show();
		}
		
		private void showGroupEditDialog(final String groupName) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(mContext.getString(R.string.new_type_edit_title));
			
			View customView = mInflater.inflate(R.layout.dialog_edit_view, null);
			final EditText etTypeName = (EditText) customView.findViewById(R.id.et_type_name);
			etTypeName.setText(groupName);
			
			builder.setView(customView);
			
			builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(Utils.isTypeNameExists(etTypeName.getText().toString())) {
						Toast.makeText(mContext, mContext.getString(R.string.name_existed), Toast.LENGTH_SHORT).show();
					} else {
						if(mDataManager.changeTypeName(groupName, etTypeName.getText().toString())) {
							((FragmentPersonal)mFgPersonal).updateData();
						} else {
							Utils.makeText(R.string.update_fail);
						}
					}
				}
			});
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
			
			builder.create().show();
		}
		
		public void setData(List<String> groupList, List<List<StreamInfo>> childList) {
			this.mGroupList = groupList;
			setChildList(childList);
		}
		
		public void setChildList(List<List<StreamInfo>> childList) {
			this.mChildList = childList;
			notifyDataSetChanged();
		}
		
		private class ChildHolder {
			TextView tv_title;
		}
		
		private class GroupHolder {
			TextView tv_type;
			ImageView iv_setting;
		}

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			try {
				Uri uri = Uri.parse(mChildList.get(groupPosition).get(childPosition).getUrl());
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				intent.setType("video/*");
				intent.setDataAndType(uri, "video/*");
				mContext.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(mContext, mContext.getString(R.string.no_software_to_play), Toast.LENGTH_SHORT).show();
			}
			return true;
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int arg2, long arg3) {
			longClickOperate(view);
			return true;
		}
		
		private void longClickOperate(View view) {
			int groupPos = (Integer) view.getTag(R.string.expandable_view_group_tag);
			int childPos = (Integer) view.getTag(R.string.expandable_view_child_tag);
			
			if(childPos != -1) {
				showChildOperateDialog(groupPos, childPos);
			}
		}
		
		private void showChildOperateDialog(final int groupPos, final int childPos) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(R.string.choose_a_operate);
			
			String[] items = {mContext.getString(childOperateStrId[0]),
								mContext.getString(childOperateStrId[1]),
								mContext.getString(childOperateStrId[2])};
			builder.setItems(items, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch(which) {
					case 0:
						if(removeItemFromType(groupPos, childPos)) {
							Utils.makeText(R.string.remove_successful);
							((FragmentPersonal)mFgPersonal).updateData();
						} else {
							Utils.makeText(R.string.remove_fail);
						}
						
						break;
						
					case 1:
						showAddAnotherGroup(groupPos, childPos);
						break;
						
					case 2:
						showChangeAnotherGroup(groupPos, childPos);
						break;
					}
				}
			});
			builder.create().show();
		}
		
		private boolean removeItemFromType(int groupPos, int childPos) {
			List<String> typeName = mDataManager.getAllTypeName();
			return mDataManager.removeItemFromType(typeName.get(groupPos + 1), mChildList.get(groupPos).get(childPos).getTitle());
		}
		
		private void showAddAnotherGroup(final int groupPos, final int childPos) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(R.string.add_to_other_group);
			
			final List<String> typeTabNameList = mDataManager.getAllTypeName();
			typeTabNameList.remove(0);
			typeTabNameList.add(mContext.getString(R.string.add_custom_type));
			String[] typeName = (String[]) typeTabNameList.toArray(new String[typeTabNameList.size()]);
			
			builder.setItems(typeName, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(which == typeTabNameList.size() - 1) {
						showNewListEditDialog(groupPos,childPos, false);
					} else {
						StreamInfo info = new StreamInfo();
						String title = mChildList.get(groupPos).get(childPos).getTitle();
						info.setTitle(title);
						info.setUrl(mChildList.get(groupPos).get(childPos).getUrl());
						if(addItemToPersonalList(info, typeTabNameList.get(which))) {
							Utils.makeText(R.string.added_successful);
							((FragmentPersonal)mFgPersonal).updateData();
						} else {
							Utils.makeText(R.string.added_fail);
						}
					}
				}
			});
			
			builder.create().show();
			
		}
		
		private void showNewListEditDialog(final int groupPos, final int childPos, final boolean isRemoveFromOld) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(mContext.getString(R.string.new_type_edit_title));
			
			View customView = mInflater.inflate(R.layout.dialog_edit_view, null);
			final EditText etTypeName = (EditText) customView.findViewById(R.id.et_type_name);
			
			builder.setView(customView);
			
			builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(Utils.isTypeNameExists(etTypeName.getText().toString())) {
						Toast.makeText(mContext, mContext.getString(R.string.name_existed), Toast.LENGTH_SHORT).show();
					} else {
						StreamInfo info = new StreamInfo();
						String title = mChildList.get(groupPos).get(childPos).getTitle();
						info.setTitle(title);
						info.setUrl(mChildList.get(groupPos).get(childPos).getUrl());
						if(addItemToPersonalList(info,etTypeName.getText().toString())) {
							
							if(!isRemoveFromOld) {
								Utils.makeText(R.string.added_successful);
								((FragmentPersonal)mFgPersonal).updateData();
							} else {
								if(removeItemFromType(groupPos, childPos)) {
									Utils.makeText(R.string.added_successful);
									((FragmentPersonal)mFgPersonal).updateData();
								}
							}
							
						} else {
							Utils.makeText(R.string.added_fail);
						}
					}
				}
			});
			builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
				
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
		
		private void showChangeAnotherGroup(final int groupPos, final int childPos) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(R.string.change_to_other_group);
			
			final List<String> typeTabNameList = mDataManager.getAllTypeName();
			typeTabNameList.remove(0);
			typeTabNameList.add(mContext.getString(R.string.add_custom_type));
			String[] typeName = (String[]) typeTabNameList.toArray(new String[typeTabNameList.size()]);
			
			builder.setItems(typeName, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(which == typeTabNameList.size() - 1) {
						showNewListEditDialog(groupPos,childPos, true);
					} else {
						StreamInfo info = new StreamInfo();
						String title = mChildList.get(groupPos).get(childPos).getTitle();
						info.setTitle(title);
						info.setUrl(mChildList.get(groupPos).get(childPos).getUrl());
						if(addItemToPersonalList(info, typeTabNameList.get(which))) {
							
							if(removeItemFromType(groupPos, childPos)) {
								Utils.makeText(R.string.changed_successful);
								((FragmentPersonal)mFgPersonal).updateData();
							}
							
							
						} else {
							Utils.makeText(R.string.added_fail);
						}
					}
				}
			});
			
			builder.create().show();
		}
	}
}
