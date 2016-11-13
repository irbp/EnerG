package goncinha.energ;

import android.support.v7.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    int counter = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginlayout);
        final EditText username = (EditText) findViewById(R.id.user);
        final EditText password = (EditText) findViewById(R.id.password);
        final Button loginbutton = (Button) findViewById(R.id.login);
        final Button register = (Button) findViewById(R.id.register);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().equals("admin") &&
                        password.getText().toString().equals("admin")) {
                    Toast.makeText(getApplicationContext(),
                            "Redirecionando...",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Email ou senha errados",Toast.LENGTH_SHORT).show();
                    counter--;
                    if (counter == 0) {
                        loginbutton.setEnabled(false);
                        Toast.makeText(getApplicationContext(),
                                "Você errou 3 vezes a senha",Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                        "Criando conta...",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
