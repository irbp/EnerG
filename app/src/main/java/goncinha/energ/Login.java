package goncinha.energ;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.R.attr.id;

public class Login extends AppCompatActivity {
    int counter = 3;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference bdRef;

    private EditText username;
    private EditText password;
    private Button loginbutton;
    private Button register;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginlayout);

        username = (EditText) findViewById(R.id.user);
        password = (EditText) findViewById(R.id.password);
        loginbutton = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);

        mProgress = new ProgressDialog(this);

        bdRef = FirebaseDatabase.getInstance().getReference().child("usuarios");
        bdRef.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Intent mInt;

                if (firebaseAuth.getCurrentUser() == null) {
                    register.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent mInt = new Intent(Login.this, RegistrarActivity.class);
                            mInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mInt);
                        }
                    });
                }
                else {
                    mInt = new Intent(Login.this, Main.class);
                    mInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mInt);
                }
            }
        };

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checarLogin();
            }
        });
    }

    private void checarLogin() {
        String email;
        String senha;

        email = username.getText().toString().trim();
        senha = password.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(senha)) {
            mProgress.setMessage("Verificando login...");
            mProgress.show();

            mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mProgress.dismiss();
                        checarUsuario();
                    }
                    else {
                        Toast.makeText(Login.this, "Erro ao tentar efetuar o login!", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    }
                }
            });
        }
        else {
            Toast.makeText(Login.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checarUsuario() {
        final String idUsuario;

        idUsuario = mAuth.getCurrentUser().getUid();

        bdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(idUsuario)) {
                    Intent mInt = new Intent(Login.this, Main.class);
                    mInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mInt);
                }
                else {
                    Intent setupInt = new Intent(Login.this, ConfigurarContaActivity.class);
                    setupInt.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupInt);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private void teste(){
        int i = 1;
    }
}
