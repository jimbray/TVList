package com.jim.common;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.widget.Toast;

import com.jim.data.DataProviderManager;
public class Utils {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList<String> getOrderStreamTitle(ArrayList<String> list) {
		ArrayList<String> resultList = new ArrayList<String>();
		
		Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);
		Object[] ob = list.toArray();
		Arrays.sort(ob, cmp);
		
		for(int i = 0 ; i < ob.length; i++) {
			resultList.add((String) ob[i]);
		}
		
		return resultList;
	}
	
	public static boolean isTypeNameExists(String name) {
		return DataProviderManager.getInstance().isTypeNameExists(name);
	}
	
	public static void makeText(String txt) {
		Toast.makeText(DataProviderManager.getAppContext(), txt, Toast.LENGTH_SHORT).show();
	}
	
	public static void makeText(int txtId) {
		Context context = DataProviderManager.getAppContext();
		Toast.makeText(context, context.getString(txtId), Toast.LENGTH_SHORT).show();
	}
	
	/**
	  * ÅÐ¶Ï×Ö·û´®ÊÇ·ñÊÇÕûÊý
	  */
	 public static boolean isInteger(String value) {
	  try {
	   Integer.parseInt(value);
	   return true;
	  } catch (NumberFormatException e) {
	   return false;
	  }
	 }

	 /**
	  * ÅÐ¶Ï×Ö·û´®ÊÇ·ñÊÇ¸¡µãÊý
	  */
	 public static boolean isDouble(String value) {
	  try {
	   Double.parseDouble(value);
	   if (value.contains("."))
	    return true;
	   return false;
	  } catch (NumberFormatException e) {
	   return false;
	  }
	 }

	 /**
	  * ÅÐ¶Ï×Ö·û´®ÊÇ·ñÊÇÊý×Ö
	  */
	 public static boolean isNumber(String value) {
	  return isInteger(value) || isDouble(value);
	 }
}
