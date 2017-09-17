package com.gohool.sqlitedb.sqlitedatabase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import Data.DatabaseHandler;
import Model.Contact;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHandler db = new DatabaseHandler(this);

        // Insert contact to the database
        Log.d("Insert: ", "Inserting...");

        // Create some contact object
        db.addContact(new Contact("maleakhi", "234"));

        // Read them back
        Log.d("Reading: ", "Reading...");
        List<Contact> contactList = db.getAllContacts();

        for (Contact c : contactList) {
            String log = "ID: " + c.getId() + " ,Name: " + c.getName() + " ,Phone: " + c.getPhoneNumber();
            Log.d("Message", log);
        }

        // Get one contact
        Contact oneContact = db.getContact(1);

        // Update contact
        int newContact = db.updateContact(oneContact); // update one contact

        // Delete contact
        db.deleteContact(oneContact); // delete onecontact which is maleakhi in this case

    }
}
