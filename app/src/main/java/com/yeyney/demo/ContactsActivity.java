package com.yeyney.demo;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ContactsActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    /*
     * Defines an array that contains column names to move from
     * the Cursor to the ListView.
     */
    private final static String[] FROM_COLUMNS = {
            ContactsContract.Contacts.DISPLAY_NAME
    };
    /*
     * Defines an array that contains resource ids for the layout views
     * that get the Cursor column contents. The id is pre-defined in
     * the Android framework, so it is prefaced with "android.R.id"
     */
    private final static int[] TO_IDS = {
            android.R.id.text1
    };
    private static final String TAG = "ContactsActivity";
    // Define global mutable variables
    // Define a ListView object
    ListView mContactsList;
    // Define variables for the contact the user selects
    // The contact's _ID value
    long mContactId;
    // The contact's LOOKUP_KEY
    String mContactKey;
    // A content URI for the selected contact
    Uri mContactUri;
    // An adapter that binds the result Cursor to the ListView
    private SimpleCursorAdapter mCursorAdapter;

    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 1;


    // Empty public constructor, required by the system
    public ContactsActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_list_view);
        mContactsList = (ListView) findViewById(android.R.id.list);

        getLoaderManager().initLoader(0, null, this);

        // Gets a CursorAdapter
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER},
                "HAS_PHONE_NUMBER > 0",
                null,
                "DISPLAY_NAME COLLATE UNICODE");
//        if (cursor != null) {
//            cursor.moveToFirst();
//            while (cursor.moveToNext()) {
//                Log.d(TAG, "DISPLAY_NAME:" + String.valueOf(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))));
//                Log.d(TAG, "HAS_PHONE_NUMER: " + String.valueOf(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))));
//                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE}, " DISPLAY_NAME = ?", new String[]{displayName}, null);
//                while (c.moveToNext()) {
//                    switch (c.getInt(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))) {
//                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
//                        case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
//                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
//                        case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
//                    }
//                    Log.d(TAG, c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
//                }
//                c.close();
//            }
//            cursor.close();
//        }
        mCursorAdapter = new SimpleCursorAdapter(this, R.layout.contacts_list_item, cursor, FROM_COLUMNS, TO_IDS, 0);
        // Sets the adapter for the ListView
        mContactsList.setAdapter(mCursorAdapter);
        mContactsList.setOnItemClickListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View item, int position, long rowID) {
        Log.d(TAG, String.valueOf(parent.getItemAtPosition(position)));
    }
}
