package com.niu.myapplication;

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
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditArticleActivity extends AppCompatActivity {
    static final int RC_PHOTO_PICKER = 1;
    StorageReference storageReference;
    EditText articleContent,articleTitle,articleImageURL;
    String editarticleRef,userUID;
    DatabaseReference usersRef,articleRef;
    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("發表問題");
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        articleTitle = (EditText) findViewById(R.id.articleTitle);
        articleContent = (EditText) findViewById(R.id.articleContent);
        articleImageURL = (EditText) findViewById(R.id.articleImageURL);
        textView = (TextView) findViewById(R.id.imagetxtview);
        storageReference = FirebaseStorage.getInstance().getReference();
        Bundle bundleSub =this.getIntent().getExtras();
        editarticleRef = bundleSub.getString("editArticleRef");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userUID = user.getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        super.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void releaseArticle() {                                        //發表文章
        String title = articleTitle.getText().toString();
        String content = articleContent.getText().toString();
        String URL = "" + articleImageURL.getText().toString();

        if (title.length() != 0 && content.length() != 0) {
            usersRef = FirebaseDatabase.getInstance().getReference(editarticleRef);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
            String str = formatter.format(curDate);

            articleRef = usersRef.child("article").push();
            Map<String, Object> article = new HashMap<>();
            article.put("date",str);
            article.put("userID", userUID);
            article.put("title", title);
            article.put("content", content);
            article.put("imageURL",URL);
            articleRef.updateChildren(article,
                    new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast toast = Toast.makeText(EditArticleActivity.this, "發表失敗!", Toast.LENGTH_LONG);
                                toast.show();
                            } else {
                                Toast toast = Toast.makeText(EditArticleActivity.this, "發表成功!", Toast.LENGTH_LONG);
                                toast.show();
                                finish();
                            }
                        }
                    });
        } else {
            Toast toast = Toast.makeText(EditArticleActivity.this, "請輸入內容!", Toast.LENGTH_LONG);
            toast.show();
        }
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

            textView.setVisibility(View.VISIBLE);
            articleImageURL.setVisibility(View.VISIBLE);

            filepath.putFile(uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // When the image has successfully uploaded, we get its download URL
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    // Set the download URL to the message box, so that the user can send it to the database
                    articleImageURL.append(downloadUrl.toString()+"\n");
                    Toast.makeText(EditArticleActivity.this, "上傳成功!", Toast.LENGTH_LONG).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_release_article) {
            releaseArticle();
            return true;
        }else if(id == R.id.action_upload_picture){
            uploadimage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
