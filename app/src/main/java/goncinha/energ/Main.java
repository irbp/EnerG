package goncinha.energ;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context mContext;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsuarios;
    private DatabaseReference mDatabaseLed;
    private DatabaseReference mDatabaseBacons;
    private DatabaseReference getmDatabaseBaconsAll;
    private DatabaseReference getmDatabaseOn;
    private StorageReference mStorage;

    private static WebView pagina;

    private boolean mProcessOn = false;
    private String status = "offline";

    private RecyclerView mBaconList;
    private TextView nomeUser;
    private TextView emailUser;
    private CircleImageView imagemUser;

    private static String content = null;

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
        mDatabaseBacons = FirebaseDatabase.getInstance().getReference().child("bacons");
        mStorage = FirebaseStorage.getInstance().getReference().child("charts");
        getmDatabaseOn = FirebaseDatabase.getInstance().getReference().child("On");
        getmDatabaseBaconsAll = FirebaseDatabase.getInstance().getReference().child("bacons_all");

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

                Picasso.with(getApplicationContext()).load(imagem).into(imagemUser);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        loadChart();
    }

    private void loadChart() {
        try {
            AssetManager assetManager = getAssets();
            InputStream in = assetManager.open("hello-pizza-chart.html");
            byte[] bytes = readFully(in);
            content = new String(bytes, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int count; (count = in.read(buffer)) != -1; ) {
            out.write(buffer, 0, count);
        }
        return out.toByteArray();
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

        if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        final String userId = mAuth.getCurrentUser().getUid().toString();

        FirebaseRecyclerAdapter<Bacon, BaconViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Bacon, BaconViewHolder>(
                Bacon.class,
                R.layout.bacon_row,
                BaconViewHolder.class,
                mDatabaseBacons.child(userId)
        ) {
            @Override
            protected void populateViewHolder(final BaconViewHolder viewHolder, final Bacon model, int position) {
                final String bacon_key = getRef(position).getKey();

                viewHolder.setNome(model.getNome());
                viewHolder.setOnline(model.getOnline());

                viewHolder.mOnButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProcessOn = true;
                        getmDatabaseOn.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (status.equals("online") && mProcessOn) {
                                    getmDatabaseOn.child(bacon_key).setValue("offline");
                                    status = "offline";
                                    mDatabaseBacons.child(userId).child(bacon_key).child("online").setValue("offline");
                                    getmDatabaseBaconsAll.child(bacon_key).child("online").setValue("offline");
                                    viewHolder.mOnButton.setImageResource(R.drawable.off1);
                                    mProcessOn = false;
                                } else if (status.equals("offline") && mProcessOn) {
                                    getmDatabaseOn.child(bacon_key).setValue("online");
                                    status = "online";
                                    getmDatabaseBaconsAll.child(bacon_key).child("online").setValue("online");
                                    mDatabaseBacons.child(userId).child(bacon_key).child("online").setValue("online");
                                    viewHolder.mOnButton.setImageResource(R.drawable.on1);
                                    mProcessOn = false;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

            }
        };

        mBaconList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BaconViewHolder extends RecyclerView.ViewHolder {
        View mView;

        ImageButton mOnButton;

        public BaconViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mOnButton = (ImageButton) mView.findViewById(R.id.btn_turnon);
        }

        public void setNome(String nome) {
            TextView mNome = (TextView) mView.findViewById(R.id.txt_baconStatus);
            mNome.setText(nome);
        }

        public void setOnline(String online) {
            TextView mOnline = (TextView) mView.findViewById(R.id.txt_baconName);
            mOnline.setText(online);

            pagina = (WebView) mView.findViewById(R.id.web_grafico);
            WebSettings ws = pagina.getSettings();
            ws.setJavaScriptEnabled(true);
            ws.setSupportZoom(false);
            pagina.setWebViewClient(new WebViewClient());
            pagina.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "utf-8", null);
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
