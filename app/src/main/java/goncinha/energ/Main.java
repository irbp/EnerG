package goncinha.energ;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsuarios;
    private DatabaseReference mDatabaseLed;
    private DatabaseReference mDatabaseBacons;

    private RecyclerView mBaconList;
    private TextView nomeUser;
    private TextView emailUser;
    private CircleImageView imagemUser;

    private String content = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String idUsuario;

        mAuth = FirebaseAuth.getInstance();
        idUsuario = mAuth.getCurrentUser().getUid();

        mDatabaseUsuarios = FirebaseDatabase.getInstance().getReference().child("usuarios");
        mDatabaseLed = FirebaseDatabase.getInstance().getReference().child("led");
        mDatabaseBacons = FirebaseDatabase.getInstance().getReference().child("bacons_all");

        mBaconList = (RecyclerView) findViewById(R.id.bacon_list);
        mBaconList.setHasFixedSize(true);
        mBaconList.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                criarBacon();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //Alterando os dados do header da nav bar para os dados do usuário
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);

        nomeUser = (TextView) hView.findViewById(R.id.txt_profileName);
        emailUser = (TextView) hView.findViewById(R.id.txt_profileEmail);
        imagemUser = (CircleImageView) hView.findViewById(R.id.img_profileImage);

        mDatabaseUsuarios.child(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nome = dataSnapshot.child("nome").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                String imagem = dataSnapshot.child("imagem").getValue(String.class);

                nomeUser.setText(nome);
                emailUser.setText(email);

                //Picasso.with(getApplicationContext()).load(imagem).into(imagemUser);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void criarBacon() {
        Intent mInt = new Intent(Main.this, Criar_Bacon.class);
        mInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mInt);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_consumo) {
            // Handle the camera action
        } else if (id == R.id.nav_perfis) {

        } else if (id == R.id.nav_configuracoes) {

        } else if (id == R.id.nav_logout) {
            logout();
        } else if (id == R.id.nav_ajuda) {

        } else if (id == R.id.nav_bug) {

        } else if (id == R.id.nav_sobre) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Bacon, BaconViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Bacon, BaconViewHolder>(
                Bacon.class,
                R.layout.bacon_row,
                BaconViewHolder.class,
                mDatabaseBacons
        ) {
            @Override
            protected void populateViewHolder(BaconViewHolder viewHolder, Bacon model, int position) {
                viewHolder.setWebPage(model.getCorrente());
                viewHolder.setOnline(model.getOnline());
            }
        };

        mBaconList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BaconViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public BaconViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setWebPage(String corrente) {
            WebView pagina = (WebView) mView.findViewById(R.id.web_grafico);
            WebSettings ws = pagina.getSettings();
            ws.setJavaScriptEnabled(true);
            ws.setSupportZoom(false);
            pagina.setWebViewClient(new WebViewClient());
            pagina.loadUrl(corrente);
        }

        public void setOnline(String online) {
            TextView mOnline = (TextView) mView.findViewById(R.id.txt_baconName);
            mOnline.setText(online);
        }
    }

    private void logout() {
        Intent backLogin = new Intent(Main.this, Login.class);

        mAuth.signOut();

        backLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(backLogin);
    }
}
