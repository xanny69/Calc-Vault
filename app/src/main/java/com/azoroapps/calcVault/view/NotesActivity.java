package com.azoroapps.calcVault.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.azoroapps.calcVault.adapter.MyAdapter;
import com.azoroapps.calcVault.R;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        final List<MyItem> dataset = new ArrayList<>();
        dataset.add(new MyItem(R.drawable.ic_plus,"Add Note"));
        dataset.add(new MyItem(R.drawable.ic_pencil,"Edit Note"));

        final MyAdapter myAdapter = new MyAdapter(this,dataset);
        ((GridView)findViewById(R.id.grdLayout)).setAdapter(myAdapter);

//        Animation animation = new AnimationUtils().loadAnimation(MainActivity.this,R.anim.fade_in);
//        GridLayoutAnimationController gridAnim=new GridLayoutAnimationController(animation,.2f,.2f);
//        ((GridView)findViewById(R.id.grdLayout)).setLayoutAnimation(gridAnim);

        ((GridView) findViewById(R.id.grdLayout)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(dataset.get(position).equals(dataset.get(0))){
                    Intent intent = new Intent(NotesActivity.this, AddActivity.class);
                    startActivity(intent);
                }else if (dataset.get(position).equals(dataset.get(1))){
                    Intent intent = new Intent(NotesActivity.this, EditActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent= new Intent(getApplicationContext(),VaultScreen.class);
        startActivity(intent);
    }
}