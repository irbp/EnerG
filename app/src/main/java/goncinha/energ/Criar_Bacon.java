package goncinha.energ;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class Criar_Bacon extends AppCompatActivity {

    private EditText idBacon;
    private Button mDebug;
    private EditText nomeBacon;
    private Switch mSwitch;
    private TextView mOnline;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseBacons;
    private DatabaseReference mDatabaseBaconsAll;
    private DatabaseReference getmDatabaseOn;
    private StorageReference mStorage;

    private Uri arquivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar__bacon);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        idBacon = (EditText) findViewById(R.id.txt_baconId);
        nomeBacon = (EditText) findViewById(R.id.txt_nomeBacon);
        mSwitch = (Switch) findViewById(R.id.switch1);
        mOnline = (TextView) findViewById(R.id.txt_on);

        mSwitch.setChecked(true);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mOnline.setText("Online");
                }
                else {
                    mOnline.setText("Offline");
                }
            }
        });

        mDebug = (Button) findViewById(R.id.deb);
        mDatabaseBacons = FirebaseDatabase.getInstance().getReference().child("bacons");
        mDatabaseBaconsAll = FirebaseDatabase.getInstance().getReference().child("bacons_all");
        mStorage = FirebaseStorage.getInstance().getReference().child("charts");
        getmDatabaseOn = FirebaseDatabase.getInstance().getReference().child("On");

        arquivo = Uri.fromFile(new File("android_asset/teste.html"));

        mAuth = FirebaseAuth.getInstance();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_createBacon);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regID();
            }
        });

        mDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent debug = new Intent(Criar_Bacon.this, Debug.class);
                debug.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(debug);
            }
        });

        if (mSwitch.isChecked()) {
            mOnline.setText("Online");
        }
        else {
            mOnline.setText("Offline");
        }
    }

    private void regID() {
        final String id;
        final String nome1;

        id = idBacon.getText().toString().trim();
        nome1 = nomeBacon.getText().toString().trim();
        if (!TextUtils.isEmpty(id)) {
            final StorageReference caminho = mStorage.child(id + ".html");
            caminho.putFile(arquivo).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    mDatabaseBaconsAll.child(id).child("grafico").setValue(downloadUri.toString());
                }
            });

            mDatabaseBacons.child(mAuth.getCurrentUser().getUid()).child(id).child("id").setValue(id);
            mDatabaseBacons.child(mAuth.getCurrentUser().getUid()).child(id).child("nome").setValue(nome1);
            mDatabaseBaconsAll.child(id).child("usuario").setValue(mAuth.getCurrentUser().getUid().toString());
            mDatabaseBaconsAll.child(id).child("nome").setValue(nome1);
            mDatabaseBaconsAll.child(id).child("id").setValue(id);
            getmDatabaseOn.child(id).setValue("online");
            if (mSwitch.isChecked()) {
                mDatabaseBacons.child(mAuth.getCurrentUser().getUid()).child(id).child("online").setValue("online");
                mDatabaseBaconsAll.child(id).child("online").setValue("online");
            }
            else {
                mDatabaseBacons.child(mAuth.getCurrentUser().getUid()).child(id).child("online").setValue("offline");
                mDatabaseBaconsAll.child(id).child("online").setValue("offline");
            }

            Intent mInt = new Intent(Criar_Bacon.this, Main.class);
            mInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(mInt);
        }
        else {
            Toast.makeText(Criar_Bacon.this, "O campo não pode ficar vazio!", Toast.LENGTH_SHORT).show();
        }
    }

}
