package com.niu.myapplication.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.niu.myapplication.EditArticleActivity;
import com.niu.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReplayActivity extends AppCompatActivity {
    static final int RC_PHOTO_PICKER = 1;
    StorageReference storageReference;
    EditText articleContent,articleImageURL;
    DatabaseReference replayRef,articleKeyRef;
    FirebaseAuth auth;
    FirebaseUser user;
    String userUID,KeyRef,articleDataRef,articleDataKEY;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("我來回答");
        getDataFromArticleActivity();
        articleContent = (EditText) findViewById(R.id.articleContent);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        articleImageURL = (EditText) findViewById(R.id.articleImageURL);
        articleImageURL = (EditText) findViewById(R.id.articleImageURL);
        storageReference = FirebaseStorage.getInstance().getReference();
        articleKeyRef = FirebaseDatabase.getInstance().getReference(articleDataRef).child("article").child(articleDataKEY);

        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userUID =user.getUid();
    }

    private void uploadimage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_PHOTO_PICKER && resultCode ==RESULT_OK){
            Uri uri = data.getData();
            StorageReference filepath = storageReference.child("photo").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // When the image has successfully uploaded, we get its download URL
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    // Set the download URL to the message box, so that the user can send it to the database
                    articleImageURL.append(downloadUrl.toString()+"\n");
                    Toast.makeText(ReplayActivity.this, "上傳成功!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressBar.setVisibility(View.VISIBLE);
                    System.out.println("Upload is " + progress + "% done");
                    int currentprogress = (int) progress;
                    progressBar.setProgress(currentprogress);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void releaseReplay() {
        String content = articleContent.getText().toString();
        String URL = "" + articleImageURL.getText().toString();

        if (content.length() != 0) {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
            String str = formatter.format(curDate);

            replayRef = articleKeyRef.child("replay").push();
            Map<String, Object> replay = new HashMap<>();
            replay.put("date",str);
            replay.put("userID", userUID);
            replay.put("replaycontent", content);
            replay.put("imageURL",URL);
            replayRef.setValue(replay);
            replayRef.updateChildren(replay,
                    new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast toast = Toast.makeText(ReplayActivity.this, "回答失敗!", Toast.LENGTH_LONG);
                                toast.show();
                            } else {
                                Toast toast = Toast.makeText(ReplayActivity.this, "回答成功!", Toast.LENGTH_LONG);
                                toast.show();
                                finish();
                            }
                        }
                    });
        } else {
            Toast toast = Toast.makeText(ReplayActivity.this, "請輸入內容!", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void getDataFromArticleActivity(){
        Bundle bundleArticle =this.getIntent().getExtras();
        articleDataRef = bundleArticle.getString("articleDataRef");
        articleDataKEY = bundleArticle.getString("articleDataKEY");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_release_article) {
            releaseReplay();
            return true;
        }else if(id == R.id.action_upload_picture){
            uploadimage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
