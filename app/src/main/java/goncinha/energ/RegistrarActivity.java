package goncinha.energ;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.net.URI;
import java.security.SecureRandom;
import java.util.Random;

public class RegistrarActivity extends AppCompatActivity {

    private char[] CHARSET_AZ_09 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    private EditText mNome;
    private EditText mEmail;
    private EditText mSenha;
    private EditText mSenhaRep;
    private Button mBtnCriar;
    private Button mBtnImagem;

    private Uri imagemUri = null;

    private FirebaseAuth mAuth;
    private DatabaseReference mBdRef;
    private StorageReference mStorage;

    private ProgressDialog mProgresso;

    private static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        mAuth = FirebaseAuth.getInstance();
        mBdRef = FirebaseDatabase.getInstance().getReference().child("usuarios");
        mStorage = FirebaseStorage.getInstance().getReference().child("fotoPerfil");

        mProgresso = new ProgressDialog(this);

        mNome = (EditText) findViewById(R.id.txt_nomereg);
        mEmail = (EditText) findViewById(R.id.txt_emailreg);
        mSenha = (EditText) findViewById(R.id.txt_senhareg);
        mSenhaRep = (EditText) findViewById(R.id.txt_confsenhareg);
        mBtnCriar = (Button) findViewById(R.id.btn_signup);
        mBtnImagem = (Button) findViewById(R.id.btn_imagem);

        mBtnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Registrar();
            }
        });

        mBtnImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galeriaIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galeriaIntent.setType("image/*");
                startActivityForResult(galeriaIntent, GALLERY_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            imagemUri = data.getData();

            CropImage.activity(imagemUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imagemUri = resultUri;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private static String randomString(char[] characterSet, int length) {
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(characterSet.length);
            result[i] = characterSet[randomCharIndex];
        }
        return new String(result);
    }

    private void Registrar() {
        final String nome;
        final String email;
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

            final StorageReference caminho = mStorage.child(randomString(CHARSET_AZ_09, 20));

            mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String id_usuario = mAuth.getCurrentUser().getUid();
                        final DatabaseReference usuario_atual = mBdRef.child(id_usuario);

                        usuario_atual.child("nome").setValue(nome);
                        usuario_atual.child("email").setValue(email);
                        usuario_atual.child("bacons").setValue("default");

                        if (imagemUri != null) {
                            caminho.putFile(imagemUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                                    usuario_atual.child("imagem").setValue(downloadUri.toString());
                                }
                            });
                        }
                        else {
                            usuario_atual.child("imagem").setValue("default");
                        }

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
                Toast.makeText(this, "As senhas devem corresponder!", Toast.LENGTH_LONG).show();
            }
            else if (senha.length() < 6 || senharep.length() < 6) {
                Toast.makeText(this, "A senha precisa ter no mínimo 6 caracteres!", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_LONG).show();
            }
        }

    }
}
