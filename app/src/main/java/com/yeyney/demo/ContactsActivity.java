package com.yeyney.demo;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class ContactsActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>, TextWatcher, View.OnClickListener {

    public static final int SEND_SMS_REQUEST = 128;

    private static final String[] PROJECTION = {
            Contacts._ID,
            Contacts.DISPLAY_NAME,
            Contacts.HAS_PHONE_NUMBER

    };
    private static final String SELECTION = "HAS_PHONE_NUMBER > 0";
    private static final String SELECTION_SEARCH = "DISPLAY_NAME like ? AND " + SELECTION;
    private static final String ORDER_BY = "DISPLAY_NAME COLLATE UNICODE";
    private static final int SEARCH_AFTER_TEXT_CHANGED = 1024;

    private ListView contactsList;
    private ContactsAdapter mCursorAdapter;
    private String mSearchString = "";

    public ContactsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        EditText searchField = (EditText) findViewById(R.id.editText_contacts_search);
        searchField.addTextChangedListener(this);

        contactsList = (ListView) findViewById(R.id.listview_contacts);

        getLoaderManager().initLoader(0, null, this);

        Cursor cursor = getContentResolver().query(Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                null,
                ORDER_BY);
        mCursorAdapter = new ContactsAdapter(this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        contactsList.setAdapter(mCursorAdapter);

        findViewById(R.id.button_contacts_send).setOnClickListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        return new CursorLoader(
                this,
                Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION_SEARCH,
                new String[]{"%" + mSearchString + "%"},
                ORDER_BY
        );
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        mSearchString = editable.toString();
        getLoaderManager().restartLoader(SEARCH_AFTER_TEXT_CHANGED, null, this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.putExtra("recipients", mCursorAdapter.getSelectedContacts());
        setResult(RESULT_OK, intent);
        finish();
    }
}
