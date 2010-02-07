package com.example.android.genericclient;

import android.os.IBinder;

public interface ISharedNotepadBinder extends IBinder {

	public ISharedNotepadService getSharedNotepadService();
}
