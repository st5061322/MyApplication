package com.niu.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niu.myapplication.LoginAndRegister.LoginActivity;
import com.niu.myapplication.RecyclerView.MainRecyclerViewAdapter;
import com.niu.myapplication.RecyclerView.MainSubject;
import com.niu.myapplication.RecyclerView.RecyclerItemClickListener;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authListener;
    DatabaseReference nicknameRef;
    String subject,subjectRef,userUID;
    private RecyclerView recyclerView;
    private MainRecyclerViewAdapter adapter;
    private List<MainSubject> subList;
    String nickname;
    TextView tvHeaderName,tvHeaderEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        tvHeaderName = (TextView) headerView.findViewById(R.id.nickname);
        tvHeaderEmail = (TextView) headerView.findViewById(R.id.email);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        subList = new ArrayList<>();
        adapter = new MainRecyclerViewAdapter(this, subList);
        nicknameRef = FirebaseDatabase.getInstance().getReference("user");
        auth = FirebaseAuth.getInstance();
        AuthListener();
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareSubjects();
        RecyclerViewItemClick();
    }

    private void RecyclerViewItemClick(){
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this,
                        recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {

                            @Override
                            public void onItemClick(View view, int position) {
                                switch (position){
                                    case 0:
                                        subject = "微積分";
                                        subjectRef ="category/math";
                                        setSubData(subject);
                                        break;
                                    case 1:
                                        subject = "電子學";
                                        subjectRef ="category/electronics";
                                        setSubData(subject);
                                        break;
                                    case 2:
                                        subject = "行動通訊";
                                        subjectRef ="category/mobilecommunication";
                                        setSubData(subject);
                                }
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                        })
        );

    }

    private void setSubData(String subject){                     //修改subjectActivity名字
        Bundle bundle = new Bundle();
        bundle.putString("subject",subject);
        bundle.putString("subjectRef",subjectRef);

        Intent intentSubject = new Intent(this,SubjectActivity.class);
        intentSubject.putExtras(bundle);
        startActivity(intentSubject);
    }

    private void prepareSubjects() {
        int[] covers = new int[]{
                R.drawable.math,
                R.drawable.electronic,
                R.drawable.mobile};
        MainSubject a = new MainSubject("微積分", covers[0]);
        subList.add(a);

        a = new MainSubject("電子學", covers[1]);
        subList.add(a);

        a = new MainSubject("行動通訊", covers[2]);
        subList.add(a);

        adapter.notifyDataSetChanged();

    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void AuthListener(){
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {//使用者未登入,跳至登入畫面
                    Intent intentLogin = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intentLogin);
                }else {//判定是否為第一次使用,如果是則要設定暱稱
                    tvHeaderEmail.setText(firebaseAuth.getCurrentUser().getEmail());
                    userUID = user.getUid();
                    nicknameRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean IDcheck = dataSnapshot.child(userUID).hasChild("nickname");
                            if(!IDcheck){
                                alertDialogSetNickname();
                            }else {
                                nickname = dataSnapshot.child(userUID).child("nickname").getValue().toString();
                                tvHeaderName.setText("您好!  "+nickname);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
    }

    private void alertDialogSetNickname(){
        final View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.alert_main, null);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("請設定您的暱稱，以發文時使用")
                .setCancelable(false)
                .setView(v)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) v.findViewById(R.id.edittext);
                        if(editText.length()!=0){
                            nicknameRef.child(userUID).child("nickname").setValue(editText.getText().toString());
                            Toast.makeText(MainActivity.this,"您的暱稱設定完畢!!",Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(MainActivity.this,"請輸入您的暱稱",Toast.LENGTH_LONG).show();
                            recreate();
                        }
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            final View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.alert_main, null);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("更改暱稱")
                    .setView(v)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText editText = (EditText) v.findViewById(R.id.edittext);
                            if(editText.length()!=0){
                                nicknameRef.child(userUID).child("nickname").setValue(editText.getText().toString());
                                Toast.makeText(MainActivity.this,"您的暱稱更改完畢!!",Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("取消",null)
                    .show();
        } else if (id == R.id.nav_login_out) {
            auth.signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        AuthListener();
    }

    @Override
    protected void onStop() {
        if (authListener != null){
            auth.removeAuthStateListener(authListener);
        }
        super.onStop();
    }
}
