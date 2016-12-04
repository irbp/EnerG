package goncinha.energ;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Criar_Bacon extends AppCompatActivity {

    private EditText idBacon;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseBacons;
    private DatabaseReference mDatabaseBaconsAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar__bacon);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        idBacon = (EditText) findViewById(R.id.txt_baconId);
        mDatabaseBacons = FirebaseDatabase.getInstance().getReference().child("bacons");
        mDatabaseBaconsAll = FirebaseDatabase.getInstance().getReference().child("bacons_all");

        mAuth = FirebaseAuth.getInstance();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_createBacon);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regID();
            }
        });
    }

    private void regID() {
        String id;

        id = idBacon.getText().toString().trim();
        if (!TextUtils.isEmpty(id)) {
            mDatabaseBacons.child(mAuth.getCurrentUser().getUid()).child(id).setValue("off");
            mDatabaseBaconsAll.child(id).child("usuario").setValue(mAuth.getCurrentUser().getUid().toString());

        }
        else {
            Toast.makeText(Criar_Bacon.this, "O campo não pode ficar vazio!", Toast.LENGTH_SHORT).show();
        }
    }

}
