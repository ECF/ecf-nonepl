/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.notepad;

import java.io.IOException;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.android.notepad.NotePad.Notes;
import com.example.android.notepad.sharednotepadclient.ISharedNotepadClient;
import com.example.android.notepad.sharednotepadclient.ISharedNotepadListener;
import com.example.android.notepad.sharednotepadclient.SharedNotepadClient;
import com.example.android.sharedobjectservice.ISharedObjectContainerService;

/**
 * Displays a list of notes. Will display notes from the {@link Uri} provided in
 * the intent if there is one, otherwise defaults to displaying the contents of
 * the {@link NotePadProvider}
 */
public class NotesList extends ListActivity {
	private static final String TAG = "NotesList";

	// Menu item ids
	public static final int MENU_ITEM_DELETE = Menu.FIRST;
	public static final int MENU_ITEM_INSERT = Menu.FIRST + 1;
	// The Android simulator client works on the special localhost address: 10.0.2.2
	private static final String CONNECT_TARGET = "ecftcp://192.168.0.14:3282/server";
	private String mOriginalContent;
	private LocationManager locationManager;
	private static final String serviceClass = "com.example.android.sharedobjectservice.SharedObjectContainerService";
	private ISharedObjectContainerService soService;
	private static final int EDIT_NOTE_REQUEST = 1;
	private static final int INSERT_NOTE_REQUEST = 0;

	private Context context;

	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * The columns we are interested in from the database
	 */
	private static final String[] PROJECTION = new String[] { 
			Notes._ID, // 0
			Notes.TITLE, // 1
	};

	/** The index of the title column */
	private static final int COLUMN_INDEX_TITLE = 1;

	// XXX sharedNotepadClient reference...see onServiceConnected below
	private ISharedNotepadClient sharedNotepadClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Intent sharedNotepadIntent;
		try {
			sharedNotepadIntent = new Intent(context, Class.forName(serviceClass, true, context.getClassLoader()));
		} catch (ClassNotFoundException e1) {
			sharedNotepadIntent=new Intent();
			sharedNotepadIntent.setComponent(new ComponentName(context,	serviceClass));
			Log.e(TAG, e1.getMessage());
		}

		// XXX sharedNotepadListener...this is passed into the shared notepad connect...see
		// onServiceConnected below...this is for handling asynchronous updates from remotes
		final ISharedNotepadListener sharedNotepadListener = new ISharedNotepadListener() {
			public void receiveUpdate(ID clientID, String username, String uri, String data) {
				System.out.println("receiveUpdate clientID=" + clientID	+ " username=" + username + " uri= "+uri + " data=" + data );
				// XXX handling of asynchronous updates should be here
				try {
					final Intent intent = new Intent(Intent.ACTION_EDIT, Uri.parse(uri));
					intent.putExtra(NoteEditor.keyData, data);
					startActivity( intent );
					
				} catch(Exception e){
					return;
				}
			}

			@Override
			public void receiveUpdate(ID clientID, String username, String uri) {
				// TODO Auto-generated method stub
				
			}

		};
		ServiceConnection serviceConnection = new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.i(TAG, "onServiceConnected name=" + name + ",service=" + service);
				soService = (ISharedObjectContainerService) service;
				// Create shared notepad client
				sharedNotepadClient = new SharedNotepadClient(soService, "pierre",	mOriginalContent, sharedNotepadListener, locationManager);
				// And connect
				try {
					sharedNotepadClient.connect(CONNECT_TARGET);
				} catch (ContainerConnectException e) {
					e.printStackTrace();
				}
				// XXX test by sending an update
				try {
					sharedNotepadClient.sendUpdate(sharedNotepadClient.getClientID(), "pierre", "startcontainer", getIntent().getDataString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.i(TAG, "onServiceDisconnected name=" + name);
				sharedNotepadClient.close();
				sharedNotepadClient = null;
			}
		};

		// Use Android OS to bind to shared notepad service. When the binding is
		// complete,
		// the onServiceConnected method is executed
		boolean bindResult = context.bindService( sharedNotepadIntent, serviceConnection, BIND_AUTO_CREATE);
		if (!bindResult) Log.e(TAG, "binding to SharedObjectContainerService failed");

		setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

		// If no data was given in the intent (because we were started
		// as a MAIN activity), then use our default content provider.
		Intent intent = getIntent();
		if (intent.getData() == null) {
			intent.setData(Notes.CONTENT_URI);
		}

		// Inform the list we provide context menus for items
		getListView().setOnCreateContextMenuListener(this);

		// Perform a managed query. The Activity will handle closing and
		// requerying the cursor
		// when needed.
		Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null,
				null, Notes.DEFAULT_SORT_ORDER);

		// Used to map notes entries from the database to views
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.noteslist_item, cursor, new String[] { Notes.TITLE },
				new int[] { android.R.id.text1 });
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// This is our one standard application action -- inserting a
		// new note into the list.
		menu.add(0, MENU_ITEM_INSERT, 0, R.string.menu_insert).setShortcut('3',
				'a').setIcon(android.R.drawable.ic_menu_add);

		// Generate any additional actions that can be performed on the
		// overall list. In a normal install, there are no additional
		// actions found here, but this allows other applications to extend
		// our menu with their own actions.
		Intent intent = new Intent(null, getIntent().getData());
		intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		menu
				.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
						new ComponentName(this, NotesList.class), null, intent,
						0, null);

		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		final boolean haveItems = getListAdapter().getCount() > 0;

		// If there are any notes in the list (which implies that one of
		// them is selected), then we need to generate the actions that
		// can be performed on the current selection. This will be a combination
		// of our own specific actions along with any extensions that can be
		// found.
		if (haveItems) {
			// This is the selected item.
			Uri uri = ContentUris.withAppendedId(getIntent().getData(),
					getSelectedItemId());

			// Build menu... always starts with the EDIT action...
			Intent[] specifics = new Intent[1];
			specifics[0] = new Intent(Intent.ACTION_EDIT, uri);
			MenuItem[] items = new MenuItem[1];

			// ... is followed by whatever other actions are available...
			Intent intent = new Intent(null, uri);
			intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
			menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0, null,
					specifics, intent, 0, items);

			// Give a shortcut to the edit action.
			if (items[0] != null) {
				items[0].setShortcut('1', 'e');
			}
		} else {
			menu.removeGroup(Menu.CATEGORY_ALTERNATIVE);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_INSERT:
			// Launch activity to insert a new item
			startActivityForResult(new Intent(Intent.ACTION_INSERT, getIntent().getData()), INSERT_NOTE_REQUEST);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfo", e);
			return;
		}

		Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
		if (cursor == null) {
			// For some reason the requested item isn't available, do nothing
			return;
		}

		// Setup the menu header
		menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_TITLE));

		// Add a menu item to delete the note
		menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfo", e);
			return false;
		}

		switch (item.getItemId()) {
		case MENU_ITEM_DELETE: {
			// Delete the note that the context menu is for
			Uri noteUri = ContentUris.withAppendedId(getIntent().getData(),
					info.id);
			getContentResolver().delete(noteUri, null, null);
			return true;
		}
		}
		return false;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);

		String action = getIntent().getAction();
		if (Intent.ACTION_PICK.equals(action)
				|| Intent.ACTION_GET_CONTENT.equals(action)) {
			// The caller is waiting for us to return a note selected by
			// the user. They have clicked on one, so return it now.
			setResult(RESULT_OK, new Intent().setData(uri));
		} else {
			// Launch activity to view/edit the currently selected item
			Log.i(TAG, "start for Result");
			startActivityForResult(new Intent(Intent.ACTION_EDIT, uri), EDIT_NOTE_REQUEST);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( requestCode==EDIT_NOTE_REQUEST || requestCode==INSERT_NOTE_REQUEST ){
			if( resultCode==RESULT_OK){
				Log.i(TAG, "action="+data.getAction());
				try {
					this.sharedNotepadClient.sendUpdate(sharedNotepadClient.getClientID(), "pierre", data.getAction(), data.getStringExtra(NoteEditor.keyData));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Log.i(TAG, "RESULT_CANCELED");
			}
		}
		
	}


}
