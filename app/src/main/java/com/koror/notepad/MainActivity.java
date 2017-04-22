package com.koror.notepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity   {

    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    ListView listView;
    SharedPreferences sPref;

    public static final String SPREFNAME="preferencename";
    public static final String ARRAYSIZEKEY="arr_size";
    public static final String ARRAYNAME="arr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        arrayList = loadList(arrayList);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arrayList);
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
                builder.setTitle(R.string.builder_title);
                final EditText input = new EditText(MainActivity.this);
                builder.setView(input);
                builder.setPositiveButton(R.string.button_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean equals=false;
                        //цикл по всем элементам
                        for(int k=0;k<arrayList.size();k++){
                            //проверка на совпадение имен
                            if(input.getText().toString().equals(arrayList.get(k).toString()))
                            {
                                Toast.makeText(getApplicationContext(),R.string.toast_error,Toast.LENGTH_SHORT).show();
                                equals=true;
                            }
                        }
                        if(!equals) {
                            arrayList.add(input.getText().toString());
                            adapter.notifyDataSetChanged();
                            //запоминает новый item id которога является размер массива - 1, так как id считается от нуля
                            addItem(input.getText().toString(),arrayList.size()-1);
                        }

                    }
                });

                builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
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
                alertBuilder.setTitle(getResources().getString(R.string.notification_of_deletion) + " " + ((TextView) itemClicked).getText() + " ?");
                alertBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //удаление элемента списка и файла
                        arrayList.remove(in);
                        deleteFile(((TextView) itemClicked).getText().toString());
                        adapter.notifyDataSetChanged();
                        deleteItem(in);
                        dialogInterface.cancel();
                    }
                });

                alertBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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
                intent.putExtra("file",((TextView) itemClicked).getText());
                startActivityForResult(intent,1);

            }
        });

    }
// запоминает новый элемент спика
    public boolean addItem(String name,int id) {
        SharedPreferences prefs = this.getSharedPreferences(SPREFNAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(ARRAYSIZEKEY, arrayList.size());
        editor.putString(ARRAYNAME + id, name);
        return editor.commit();
    }
//забывает элемент списка
    public boolean deleteItem(int id) {
        SharedPreferences prefs = this.getSharedPreferences(SPREFNAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(ARRAYSIZEKEY,arrayList.size());
        editor.remove(ARRAYNAME + id);
        return editor.commit();
    }

    public ArrayList<String> loadList(ArrayList<String> array) {
        SharedPreferences sPref = this.getSharedPreferences(SPREFNAME, 0);
        int size = sPref.getInt(ARRAYSIZEKEY, 0);
        for(int i=0;i<size;i++)
        {
            array.add(i,sPref.getString(ARRAYNAME + i, null));
        }
        return array;
    }

}
