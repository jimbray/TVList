package com.jim.myinterface;

public interface RequestListener {
	void OnGetDataBegin();
	void OnGetDataChange();
	void OnGetDataComplete(Object response);
	void OnGetDataException(Exception e);
}

