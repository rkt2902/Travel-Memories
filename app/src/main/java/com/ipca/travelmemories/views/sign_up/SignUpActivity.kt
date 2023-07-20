package com.ipca.travelmemories.views.sign_up

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.ipca.travelmemories.MainActivity
import com.ipca.travelmemories.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ao clicar no botão de criar conta
        binding.buttonSignUpSignUp.setOnClickListener {
            val name = binding.editTextSignUpName.text.toString()
            val email = binding.editTextSignUpEmail.text.toString()
            val password = binding.editTextSignUpPassword.text.toString()

            // criar conta com os dados inseridos pelo utilizador
            viewModel.registerUserToFirebase(name, password, email) { response ->
                // ir para a página principal
                response.onSuccess {
                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                // mostrar erro
                response.onFailure {
                    Toast.makeText(
                        baseContext,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}