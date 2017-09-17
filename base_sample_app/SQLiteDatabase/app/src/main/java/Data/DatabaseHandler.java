package Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Model.Contact;
import Util.Util;

/**
 * Created by keyst on 17/09/2017.
 */
/** This class will be called from main activity */
public class DatabaseHandler extends SQLiteOpenHelper {

    // Constructor
    public DatabaseHandler(Context context) {
        // factory = null for now
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    // Create table
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create SQL to create table
        String CREATE_CONTACT_TABLE = "CREATE TABLE contacts(id INTEGER PRIMARY KEY, name TEXT, phone_number TEXT);";


        // Execute SQL
        db.execSQL(CREATE_CONTACT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Util.TABLE_NAME);

        // Create table again
        onCreate(db);
    }

    // Add contact
    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues value = new ContentValues(); // data structure to store each value
        value.put(Util.KEY_NAME, contact.getName()); // the column will be key name and the value will be the get name
        value.put(Util.KEY_PHONE_NUMBER, contact.getPhoneNumber());

        // Insert to row
        db.insert(Util.TABLE_NAME, null, value);
        db.close(); // close connection
    }

    // Get contact
    public Contact getContact(int id) {
        SQLiteDatabase db = this.getWritableDatabase(); // get from the super class

        // Class to iterate through database
        Cursor cursor = db.query(Util.TABLE_NAME, new String[] {Util.KEY_ID, Util.KEY_NAME, Util.KEY_PHONE_NUMBER}, Util.KEY_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);


        if (cursor != null) {
           cursor.moveToFirst();
        }

        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2)); // here 0, 1, 2 is the column index

        return contact;
    }

    // Get all contacts
    public List<Contact> getAllContacts() {
        SQLiteDatabase db = this.getWritableDatabase();

        List<Contact> contactList = new ArrayList<>();

        // Select all contacts
        String selectAll = "SELECT * FROM " + Util.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectAll, null); // contain all of the contacts

        // Loop through our contacts
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));

                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        return contactList;
    }

    // Update contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Util.KEY_NAME, contact.getName());
        values.put(Util.KEY_PHONE_NUMBER, contact.getPhoneNumber());

        // Update row
        return db.update(Util.TABLE_NAME, values, Util.KEY_ID + "=?", new String[] {String.valueOf(contact.getId())});
    }

    // Delete single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Util.TABLE_NAME, Util.KEY_ID, new String[] {String.valueOf(contact.getId())});

        db.close();
    }

    // Get contact count
    public int getContactsCount() {
        String countQuery = "SELECT * FROM " + Util.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }
}
