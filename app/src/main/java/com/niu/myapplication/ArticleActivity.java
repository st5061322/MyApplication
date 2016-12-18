package com.niu.myapplication;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.niu.myapplication.RecyclerView.Article;
import com.niu.myapplication.RecyclerView.SpacesItemDecoration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ArticleActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    StorageReference storageReference;
    DatabaseReference articleKeyRef,replayRef,nicknameRef,articleRef;
    String userUID,articleDataRef,articleDataKEY,artKey,articleAutherUID,Nickname,articleTitle;
    RecyclerView recyclerView;
    EditText replaycontent;
    TextView nickname,date,content;
    private FirebaseRecyclerAdapter<Article,ViewHolder> adapter;
    static final int RC_PHOTO_PICKER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        nickname = (TextView)findViewById(R.id.nickname);
        date = (TextView)findViewById(R.id.date);
        content = (TextView)findViewById(R.id.content);

        getDataFromSubjectActivity();
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        userUID =user.getUid();
        articleKeyRef = FirebaseDatabase.getInstance().getReference(articleDataRef).child("article").child(articleDataKEY);
        articleRef = FirebaseDatabase.getInstance().getReference(articleDataRef).child("article");
        nicknameRef = FirebaseDatabase.getInstance().getReference("user");
        setArticle();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        serAdapter();
        super.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView nickName,Content,date;
        public ViewHolder(View itemView) {
            super(itemView);
            nickName = (TextView)itemView.findViewById(R.id.nickname);
            date = (TextView)itemView.findViewById(R.id.date);
            Content =(TextView)itemView.findViewById(R.id.replaycontent);

        }
    }

    private void setArticle(){
        articleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren() ){
                    artKey = ds.getKey().toString();
                    if (artKey.equals(articleDataKEY)){
                        articleAutherUID = ds.child("userID").getValue().toString();
                        articleTitle = ds.child("title").getValue().toString();
                        setTitle(articleTitle);
                        date.setText(ds.child("date").getValue().toString());
                        content.setText(ds.child("content").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {//讀取資料失敗

            }
        });

        nicknameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String userKEY = ds.getKey().toString();
                    if(userKEY.equals(articleAutherUID)){
                        Nickname = ds.child("nickname").getValue().toString();
                        nickname.setText(Nickname);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDataFromSubjectActivity(){
        Bundle bundleArticle =this.getIntent().getExtras();
        articleDataRef = bundleArticle.getString("articleDataRef");
        articleDataKEY = bundleArticle.getString("articleDataKEY");
    }

    private void serAdapter(){
        adapter = new FirebaseRecyclerAdapter<Article, ViewHolder>(
                Article.class,
                R.layout.item_article,
                ViewHolder.class,
                articleKeyRef.child("replay")) {

            @Override
            protected void populateViewHolder(final ViewHolder viewHolder, final Article model, int position) {
                final String client = model.getUserID().toString();
                nicknameRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            String userKEY = ds.getKey().toString();
                            if(userKEY.equals(client)){
                                viewHolder.nickName.setText(ds.child("nickname").getValue().toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                viewHolder.date.setText(model.getDate());
                viewHolder.Content.setText(model.getReplaycontent());
            }
        };
        recyclerView.addItemDecoration(new SpacesItemDecoration(this));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void replayAlertDialogEvent(View view){
        final View v = LayoutInflater.from(ArticleActivity.this).inflate(R.layout.alert_article, null);
        new AlertDialog.Builder(ArticleActivity.this)
                .setTitle("我來回答")
                .setView(v)
                .setPositiveButton("回答", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) v.findViewById(R.id.edittext);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                        String str = formatter.format(curDate);
                        if(editText.length()!=0){
                            replayRef = articleKeyRef.child("replay").push();
                            Map<String,Object> replay = new HashMap<>();
                            replay.put("userID",userUID);
                            replay.put("date",str);
                            replay.put("replaycontent",editText.getText().toString());
                            replayRef.setValue(replay);
                            Toast.makeText(ArticleActivity.this,"回覆完成!!",Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("取消",null)
                .show();
    }
}
