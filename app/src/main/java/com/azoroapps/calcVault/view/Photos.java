package com.azoroapps.calcVault.view;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.azoroapps.calcVault.R;
import com.azoroapps.calcVault.adapter.AlbumAdapter;
import com.azoroapps.calcVault.utilities.RealPathUtil;
import com.azoroapps.calcVault.adapter.PhotosAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class Photos extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public boolean is_photos_in_action_mode=false;
    TextView photos_counter_text_view;

    RecyclerView recyclerView;
    PhotosAdapter photosAdapter;
    ArrayList<ImageDetails> img = new ArrayList<>();
    ImageDetails imageDetails;
    String[] names;
    ArrayList <ImageDetails>photo_selection_list=new ArrayList<>();
    int photo_counter=0;
    Toolbar toolbar_photos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        toolbar_photos=findViewById(R.id.toolbar_photos);
        setSupportActionBar(toolbar_photos);

        photos_counter_text_view=findViewById(R.id.counter_text_photos);
        photos_counter_text_view.setVisibility(View.GONE);


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        photosAdapter = new PhotosAdapter(this, getData(),names);
        recyclerView = findViewById(R.id.photo_id);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(photosAdapter);
    }



    private ArrayList<ImageDetails> getData() {
        Intent intent = getIntent();
        String albumName = Objects.requireNonNull(intent.getExtras()).getString("AlbumName");
        File photoPath= new File((Environment.getExternalStorageDirectory().getPath() + "/.Vault/.Photos/" + albumName + "/"));
        if (photoPath.exists()) {
            File[] files = photoPath.listFiles();
            for (File file : files) {
                imageDetails = new ImageDetails();
                imageDetails.setName(file.getName());
                imageDetails.setUri(Uri.fromFile(file));
                img.add(imageDetails);
                //imageLocation.add(Uri.fromFile(file).toString());
            }
            names = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                names[i] = files[i].getPath();
            }
        }
        return img;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.AddNewPhotos) {
            launchGalleryIntent();
        }
        if(item.getItemId()==R.id.select_photos){
            toolbar_photos.getMenu().clear();
            toolbar_photos.inflateMenu(R.menu.menu_actionmode_photos);
            photos_counter_text_view.setVisibility(View.VISIBLE);
            is_photos_in_action_mode=true;
            photosAdapter.notifyDataSetChanged();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if(item.getItemId()==R.id.photo_delete){
            Toasty.success(this,"TODO",Toasty.LENGTH_SHORT).show();
            //TODO
            photosAdapter.updateAdapter(photo_selection_list);
            clearActionMode();
            photosAdapter.notifyDataSetChanged();
        }
        else if(item.getItemId()==android.R.id.home){
            clearActionMode();
            photosAdapter.notifyDataSetChanged();
        }
        return true;
    }

    private void clearActionMode() {
        is_photos_in_action_mode=false;
        toolbar_photos.getMenu().clear();
        toolbar_photos.inflateMenu(R.menu.menu_photos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        photos_counter_text_view.setVisibility(View.GONE);
        photos_counter_text_view.setText("0 Items Selected");
        photo_counter=0;
        photo_selection_list.clear();
    }

    public void prepareSelection(View view,int position){
        if(((CheckBox)view).isChecked()){
            photo_selection_list.add(img.get(position));
            photo_counter++;
        }
        else{
            photo_selection_list.remove(img.get(position));
            photo_counter--;
        }
        updateCounter(photo_counter);
    }

    public void updateCounter(int photo_counter){
        if(photo_counter==0){
            photos_counter_text_view.setText("0 Items Selected");
        }
        else{
            photos_counter_text_view.setText(photo_counter+"items Selected");
        }
    }

    public void launchGalleryIntent() {
        Intent i = getIntent();
        String albumName = Objects.requireNonNull(i.getExtras()).getString("AlbumName");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 22);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = getIntent();
        String albumName = Objects.requireNonNull(intent.getExtras()).getString("AlbumName");
        if (requestCode==22 && resultCode==RESULT_OK&& data!=null) {
            int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }
            ClipData clipData = data.getClipData();
            String outputPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/.Vault/.Photos/"+albumName+File.separator;
            if(clipData==null){
                //Single Image Selection
                Uri uri = data.getData();
                assert uri != null;
                String lp;
                lp = RealPathUtil.getRealPath(this, uri);
                // Get the file instance
                File file = new File(lp);
                String inputPath= file.getParent()+"/";
                String inputFile = file.getName();
                moveFile(inputPath,inputFile,outputPath);
            }
            else{
                //Multiple Images Selection
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    String path;
                    path = RealPathUtil.getRealPath(Photos.this, uri);
                    // Get the file instance
                    File file = new File(path);
                    String inputPath= file.getParent() +"/";
                    String inputFile = file.getName();
                    moveFile(inputPath,inputFile,outputPath);
                }
            }
        }
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onBackPressed() {
        if(is_photos_in_action_mode){
            clearActionMode();
            photosAdapter.notifyDataSetChanged();
        }
        else{
            super.onBackPressed();
        }

    }

    private void moveFile(String inputPath, String inputFile, String outputPath) {
        InputStream in;
        OutputStream out;
        try {
            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                boolean b =dir.mkdirs();
                if(b){
                    Toasty.info(this,"Hiding Images",Toasty.LENGTH_SHORT).show();
                }
            }
            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            // write the output file
            out.flush();
            out.close();
            // delete the original file
            new File(inputPath + inputFile).delete();
            //Toasty.info(this,"Files Hidden, Please Delete the Original Images",Toasty.LENGTH_SHORT).show();
        }
        catch (FileNotFoundException f) {
            Log.e("File", f.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

}

