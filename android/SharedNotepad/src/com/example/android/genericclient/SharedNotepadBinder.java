package com.example.android.genericclient;

import android.os.Binder;

public class SharedNotepadBinder extends Binder implements ISharedNotepadBinder {

	private ISharedNotepadService service;
	
	public SharedNotepadBinder(ISharedNotepadService service) {
		this.service = service;
	}
	
	public ISharedNotepadService getSharedNotepadService() {
		return service;
	}

}
