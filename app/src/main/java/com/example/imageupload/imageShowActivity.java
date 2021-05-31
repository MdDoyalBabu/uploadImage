package com.example.imageupload;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.imageupload.Adapter.Myadapter;
import com.example.imageupload.handler.Upload;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class imageShowActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Myadapter myadapter;
    private List<Upload>uploadList;
    private ProgressBar progressBar;

    DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);

        databaseReference= FirebaseDatabase.getInstance().getReference("UplodImage");
        firebaseStorage=FirebaseStorage.getInstance();

        recyclerView=findViewById(R.id.recyclerView_id);
        progressBar=findViewById(R.id.progressbar_show_id);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        uploadList=new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                uploadList.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Upload upload=snapshot1.getValue(Upload.class);
                    upload.setKey(snapshot1.getKey());
                    uploadList.add(upload);
                }

                myadapter=new Myadapter(imageShowActivity.this,uploadList);
                recyclerView.setAdapter(myadapter);

                // onItem Click lisner add

                myadapter.setOnitemClickLisner(new Myadapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String text=uploadList.get(position).getImageName();
                        Toast.makeText(imageShowActivity.this, text+"is selected"+position, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDoAnyTask(int position) {
                        Toast.makeText(imageShowActivity.this, "OnDoAnyTask", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDelete(int position) {
                        Upload selectedItem=uploadList.get(position);
                        final String key=selectedItem.getKey();


                        StorageReference storageReference=firebaseStorage.getReferenceFromUrl(selectedItem.getImageIUri());

                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseReference.child(key).removeValue();
                                Toast.makeText(imageShowActivity.this, "Alhamdulillah item delete", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });



                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(imageShowActivity.this, "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }
}