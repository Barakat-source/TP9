package ma.projet.gestiondesexceptionserreursetcontrledeschamps;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ma.projet.gestiondesexceptionserreursetcontrledeschamps.database.UserDao;

public class SignUpActivity extends AppCompatActivity {

    private EditText nomEditText, ageEditText, emailEditText, passwordEditText;
    private Button validateButton;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userDao = new UserDao(this);
        userDao.open();

        nomEditText = findViewById(R.id.nomEditText);
        ageEditText = findViewById(R.id.ageEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        validateButton = findViewById(R.id.validateButton);

        validateButton.setEnabled(false);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                verifierFormulaire();
            }
        };

        nomEditText.addTextChangedListener(textWatcher);
        ageEditText.addTextChangedListener(textWatcher);
        emailEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validerFormulaire();
            }
        });
    }

    private boolean validateNom() {
        String nom = nomEditText.getText().toString().trim();
        if (nom.isEmpty()) {
            nomEditText.setError("Nom obligatoire");
            return false;
        } else {
            nomEditText.setError(null);
            return true;
        }
    }

    private boolean validateAge() {
        String ageStr = ageEditText.getText().toString().trim();
        if (ageStr.isEmpty()) {
            ageEditText.setError("Âge obligatoire");
            return false;
        }
        try {
            int age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 120) {
                ageEditText.setError("Âge invalide");
                return false;
            } else {
                ageEditText.setError(null);
                return true;
            }
        } catch (NumberFormatException e) {
            ageEditText.setError("Âge non numérique");
            return false;
        }
    }

    private boolean validateEmail() {
        String email = emailEditText.getText().toString().trim();
        if (email.isEmpty()) {
            emailEditText.setError("Email obligatoire");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email incorrect");
            return false;
        } else {
            emailEditText.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String password = passwordEditText.getText().toString().trim();
        if (password.isEmpty()) {
            passwordEditText.setError("Mot de passe obligatoire");
            return false;
        } else if (password.length() < 6) {
            passwordEditText.setError("Mot de passe trop court");
            return false;
        } else {
            passwordEditText.setError(null);
            return true;
        }
    }

    private void verifierFormulaire() {
        boolean nomValide = validateNom();
        boolean ageValide = validateAge();
        boolean emailValide = validateEmail();
        boolean passwordValide = validatePassword();
        validateButton.setEnabled(nomValide && ageValide && emailValide && passwordValide);
    }

    private void validerFormulaire() {
        // Re-run validation for all fields for safety before submitting
        boolean isFormValid = validateNom() & validateAge() & validateEmail() & validatePassword();

        if (isFormValid) {
            String nom = nomEditText.getText().toString().trim();
            String ageStr = ageEditText.getText().toString().trim();
            int age = Integer.parseInt(ageStr);
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            userDao.createUser(nom, age, email, password);
            Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Veuillez corriger les erreurs.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        userDao.close();
        super.onDestroy();
    }
}
