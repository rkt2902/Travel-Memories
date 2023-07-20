package com.ipca.travelmemories.views.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.ipca.travelmemories.R
import com.ipca.travelmemories.MainActivity
import com.ipca.travelmemories.databinding.ActivityAuthBinding
import com.ipca.travelmemories.views.sign_up.SignUpActivity

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ao clicar no botão de iniciar sessão
        binding.buttonAuthSignIn.setOnClickListener {
            val email = binding.editTextAuthEmail.text.toString()
            val password = binding.editTextAuthPassword.text.toString()

            // iniciar sessão do utilizador
            viewModel.loginUserFromFirebase(email, password) { response ->
                // ir para a página inicial
                response.onSuccess {
                    val intent = Intent(this@AuthActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                // mostrar erro
                response.onFailure {
                    Toast.makeText(baseContext, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // ao clicar no botão de criar conta
        binding.buttonAuthSignUp.setOnClickListener {
            // ir para a página de criar conta
            val intent = Intent(this@AuthActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}