package goncinha.energ;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class RegistrarActivity extends AppCompatActivity {

    private EditText mNome;
    private EditText mEmail;
    private EditText mSenha;
    private EditText mSenhaRep;
    private Button mBtnCriar;

    private FirebaseAuth mAuth;
    private DatabaseReference mBdRef;

    private ProgressDialog mProgresso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        mAuth = FirebaseAuth.getInstance();
        mBdRef = FirebaseDatabase.getInstance().getReference().child("usuarios");

        mProgresso = new ProgressDialog(this);

        mNome = (EditText) findViewById(R.id.txt_nomereg);
        mEmail = (EditText) findViewById(R.id.txt_emailreg);
        mSenha = (EditText) findViewById(R.id.txt_senhareg);
        mSenhaRep = (EditText) findViewById(R.id.txt_confsenhareg);
        mBtnCriar = (Button) findViewById(R.id.btn_signup);

        mBtnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Registrar();
            }
        });
    }

    private void Registrar() {
        final String nome;
        String email;
        String senha;
        String senharep;

        nome = mNome.getText().toString().trim();
        email = mEmail.getText().toString().trim();
        senha = mSenha.getText().toString().trim();
        senharep = mSenhaRep.getText().toString().trim();

        if (!TextUtils.isEmpty(nome) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(senha) &&
                senharep.equals(senha)) {
            mProgresso.setMessage("Criando conta...");
            mProgresso.show();

            mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String id_usuario = mAuth.getCurrentUser().getUid();
                        DatabaseReference usuario_atual = mBdRef.child(id_usuario);

                        usuario_atual.child("nome").setValue(nome);
                        usuario_atual.child("imagem").setValue("default");

                        mProgresso.dismiss();

                        Intent mainIntent = new Intent(RegistrarActivity.this, Main.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(mainIntent);
                    }
                }
            });
        }
        else {
            if (!senharep.equals(senha)) {
                Toast.makeText(this, "As senhas deve corresponder!", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_LONG).show();
            }
        }

    }
}
