package com.niu.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.niu.myapplication.RecyclerView.Article;
import com.niu.myapplication.RecyclerView.RecyclerItemClickListener;
import com.niu.myapplication.RecyclerView.SpacesItemDecoration;

public class SubjectActivity extends AppCompatActivity {


    String subjectRef;
    DatabaseReference ArticleRef;
    RecyclerView recyclerView;
    private LinearLayoutManager ReverseLayoutManager;
    private FirebaseRecyclerAdapter<Article, ViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(((LocationApplication)getApplication()).getLocation());
        subjectRef = ((LocationApplication)getApplication()).getLocationRef();

        ArticleRef = FirebaseDatabase.getInstance().getReference(subjectRef).child("article");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("editArticleRef",subjectRef);
                Intent intent = new Intent(SubjectActivity.this,EditArticleActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        super.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        adapter = new FirebaseRecyclerAdapter<Article, ViewHolder>(
                Article.class,
                R.layout.item_suject,
                ViewHolder.class,
                ArticleRef){

            @Override
            protected void populateViewHolder(ViewHolder viewHolder, Article model, int position) {
                viewHolder.titleView.setText(model.getTitle());
                viewHolder.dateView.setText("發表於"+model.getDate());
            }
        };
        adapter.notifyDataSetChanged();
        updateFromTop();
        recyclerView.setLayoutManager(ReverseLayoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(this));
        recyclerView.setAdapter(adapter);
        recyclerViewItmListener();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleView,dateView;
        public ViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView)itemView.findViewById(R.id.subject_title);
            dateView =(TextView)itemView.findViewById(R.id.subject_date);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }

    private void recyclerViewItmListener(){
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this,
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {//按一下時
                                String articleDataKEY = adapter.getRef(position).getKey().toString();

                                Bundle bundle = new Bundle();
                                bundle.putString("articleDataKEY",articleDataKEY);
                                bundle.putString("articleDataRef",subjectRef);

                                Intent articleIntent = new Intent(SubjectActivity.this,ArticleActivity.class);
                                articleIntent.putExtras(bundle);
                                startActivity(articleIntent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {//長按時

                            }
                        })
        );
    }

    private void updateFromTop(){
        ReverseLayoutManager = new LinearLayoutManager(SubjectActivity.this);
        ReverseLayoutManager.setReverseLayout(true);
        ReverseLayoutManager.setStackFromEnd(true);
    }
}
