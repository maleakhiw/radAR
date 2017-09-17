package com.gohool.todolist.todolist;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private Button save;
    private EditText todo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        save = (Button) findViewById(R.id.save);
        todo = (EditText) findViewById(R.id.todo);
        
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!todo.getText().toString().equals("")) {
                    String message = todo.getText().toString();
                    writeToFile(message);
                } else {
                     // do nothing for now
                }
            }
        });
        
        // read message
        if (readFromFile() != null) {
            todo.setText(readFromFile());
        }
    }
    
    private void writeToFile(String message) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("todolist.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(message);
            
            // always close your stream
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String readFromFile() {
        String result = null;
        try {
            InputStream inputStream = openFileInput("todolist.txt");
            
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                // Storage for string
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                
                String tempString = "";
                StringBuilder stringBuilder = new StringBuilder();
                
                while((tempString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(tempString);        
                }
                
                inputStream.close();
                result = stringBuilder.toString();
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
