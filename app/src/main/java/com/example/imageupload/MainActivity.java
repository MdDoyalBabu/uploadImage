package com.example.imageupload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.imageupload.handler.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


   private Button choseImageBtn,saveImageBtn,showDisplayBtn;
   private EditText imageNameEdittext;
   private ImageView imageView;
   private Uri imageUri;
   private  UploadTask uploadTask;

   DatabaseReference databaseReference;
   StorageReference storageReference;

   private static final  int IMAGE_REQUEST=1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        databaseReference= FirebaseDatabase.getInstance().getReference("UplodImage");
        storageReference= FirebaseStorage.getInstance().getReference("UplodImage");

        choseImageBtn=findViewById(R.id.chose_btn_id);
        saveImageBtn=findViewById(R.id.saveImage_id);
        showDisplayBtn=findViewById(R.id.show_image_id);

        imageNameEdittext=findViewById(R.id.edittext_id);
        imageView=findViewById(R.id.imageview_id);

        choseImageBtn.setOnClickListener(this);
        saveImageBtn.setOnClickListener(this);
        showDisplayBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.chose_btn_id:
                openFileChoser();
                break;

                case R.id.saveImage_id:
                    if(uploadTask!=null && uploadTask.isInProgress()){
                        Toast.makeText(this, "Uploading is Progress", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        saveData();
                    }

                    break;

                    case R.id.show_image_id:

                         Intent intent=new Intent(MainActivity.this,imageShowActivity.class);
                         startActivity(intent);

                        break;

        }

    }

    // file  get Extension
    public String getFileExtension(Uri imageUri){

        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));

    }


    private void saveData() {

        String imageName=imageNameEdittext.getText().toString().trim();

        if (imageName.isEmpty()){
            imageNameEdittext.setText("Enter the image name");
            imageNameEdittext.requestFocus();
            return;
        }

        StorageReference ref=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));

       ref.putFile(imageUri)
              .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                  @Override
                  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                      Toast.makeText(MainActivity.this, "image is stored successful alhamdulillah", Toast.LENGTH_SHORT).show();

                      Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                      while ((!uriTask.isSuccessful()));
                      Uri downloadUri=uriTask.getResult();


                      Upload upload=new Upload(imageName,downloadUri.toString());
                      String uplodId=databaseReference.push().getKey();

                      databaseReference.child(uplodId).setValue(upload);

                  }
              }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {

              Toast.makeText(MainActivity.this, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
          }
      });


    }

    private void openFileChoser() {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            imageUri=data.getData();
            Picasso.get().load(imageUri).into(imageView);
        }

    }
}