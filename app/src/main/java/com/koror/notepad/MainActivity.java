package com.koror.notepad;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity   {
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    ListView listView;
    SharedPreferences sPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<String>();
        arrayList = loadText(arrayList);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(adapter);

        listListener();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_add:
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Введите название.");
                final EditText input = new EditText(MainActivity.this);
                builder.setView(input);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean equals=false;
                        //цикл по всем элементам
                        for(int k=0;k<arrayList.size();k++){
                            //проверка на совпадение имен
                            if(input.getText().toString().equals(arrayList.get(k).toString()))
                            {
                                Toast.makeText(getApplicationContext(),"Файл с таким именем уже существует",Toast.LENGTH_SHORT).show();
                                equals=true;
                            }
                        }
                        if(!equals) {
                            arrayList.add(input.getText().toString());
                            adapter.notifyDataSetChanged();
                            saveText(arrayList);
                        }

                    }
                });

                builder.setNegativeButton("Cancal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void listListener()
    {

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView,  final View itemClicked, final int in, long l) {

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                alertBuilder.setTitle("Вы хотите удалить файл " + ((TextView) itemClicked).getText() + " ?");
                alertBuilder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(),((TextView) itemClicked).getText()+" delete." , Toast.LENGTH_SHORT).show();
                        arrayList.remove(in);
                        deleteFile(((TextView) itemClicked).getText().toString());
                        adapter.notifyDataSetChanged();
                        saveText(arrayList);
                        dialogInterface.cancel();
                    }
                });

                alertBuilder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View itemClicked, int i, long l) {
                Intent intent = new Intent(MainActivity.this,EditActivity.class);
                intent.putExtra("ddd",((TextView) itemClicked).getText());
                startActivityForResult(intent,1);

            }
        });

    }

    public boolean saveText(ArrayList<String> array) {
        SharedPreferences prefs = this.getSharedPreferences("preferencename", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("arr_size", array.size());
        for(int i=0;i<array.size();i++)
            editor.putString("arr" + i, array.get(i));
        return editor.commit();
    }

    public ArrayList<String> loadText(ArrayList<String> array) {
        SharedPreferences sPref = this.getSharedPreferences("preferencename", 0);
        int size = sPref.getInt("arr_size", 0);
        for(int i=0;i<size;i++)
        {
            array.add(i,sPref.getString("arr" + i, null));
        }
        return array;

    }

}
