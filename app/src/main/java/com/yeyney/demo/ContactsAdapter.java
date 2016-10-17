package com.yeyney.demo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.yeyney.demo.model.Contact;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.provider.ContactsContract.CommonDataKinds.Phone;
import static android.provider.ContactsContract.Contacts;

public class ContactsAdapter extends CursorAdapter implements View.OnClickListener {

    private static final String[] PROJECTION = new String[]{
            Phone.NUMBER,
            Phone.TYPE,
            Phone.IS_PRIMARY
    };
    private static final String SELECTION = "DISPLAY_NAME = ?";
    private static final String SORT_BY = "IS_PRIMARY DESC";
    private LayoutInflater inflater;
    private PhoneNumberUtil numberUtil;
    private HashMap<String, Contact> contacts;

    public ContactsAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        inflater = LayoutInflater.from(context);
        numberUtil = PhoneNumberUtil.getInstance();
        contacts = new HashMap<>();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.contacts_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameView = (TextView) view.findViewById(R.id.textView_contacts_name);
        String displayName = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));
        nameView.setText(String.valueOf(displayName));

        String number = getPhoneNumer(context.getContentResolver(), displayName);
        TextView numberView = (TextView) view.findViewById(R.id.textView_contacts_number);
        numberView.setText(number);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox_contacts);
        checkBox.setChecked(contacts.get(displayName).isSelected());

        view.setOnClickListener(this);
        view.findViewById(R.id.checkBox_contacts).setOnClickListener(this);
    }

    private String getPhoneNumer(ContentResolver contentResolver, String displayName) {
        Cursor cursor = contentResolver.query(Phone.CONTENT_URI,
                PROJECTION,
                SELECTION,
                new String[]{displayName},
                SORT_BY);
        cursor.moveToNext();
        Contact contact = new Contact(displayName); // TODO: A lot of useless objects generated here
        String plainNumber = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
        try {
            Phonenumber.PhoneNumber number = numberUtil.parse(plainNumber, "NO");
            contact.setNumber(number);
            return String.valueOf(number.getNationalNumber());
        } catch (NumberParseException e) {
            contact.setNumber(plainNumber);
            return plainNumber;
        } finally {
            cursor.close();
            if (!contacts.containsKey(displayName)) {
                contacts.put(contact.getDisplayName(), contact);
            }
        }
    }

    @Override
    public void onClick(View view) {
        TextView nameView = (TextView) view.findViewById(R.id.textView_contacts_name);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox_contacts);
        if (nameView == null) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            nameView = (TextView) viewGroup.findViewById(R.id.textView_contacts_name);
        } else {
            checkBox.toggle();
        }

        String displayName = String.valueOf((nameView).getText());
        contacts.get(displayName).toggleSelect();
    }

    public ArrayList<Contact> getSelectedContacts() {
        ArrayList<Contact> selected = new ArrayList<>();
        for (Contact contact : contacts.values()) {
            if (contact.isSelected()) {
                selected.add(contact);
            }
        }
        return selected;
    }
}
