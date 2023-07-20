package com.ipca.travelmemories.views.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ipca.travelmemories.R
import com.ipca.travelmemories.databinding.FragmentProfileBinding
import com.ipca.travelmemories.models.UserModel
import com.ipca.travelmemories.views.auth.AuthActivity

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var user: UserModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // carregar os dados do utilizador nas caixas de texto
        viewModel.getUserFromFirebase().observe(viewLifecycleOwner) { response ->
            response.onSuccess {
                user = it
                binding.editTextProfileCountry.setText(user.country)
                binding.editTextProfileName.setText(user.name)
                binding.editTextProfileEmail.setText(user.email)
            }
            response.onFailure {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        // ao clicar em atualizar dados
        binding.buttonProfileUpdateData.setOnClickListener {
            val name = binding.editTextProfileName.text.toString()
            val country = binding.editTextProfileCountry.text.toString()

            viewModel.editUserDataFromFirebase(name, country) { response ->
                // ir para o ecrã principal
                response.onSuccess {
                    findNavController().navigate(R.id.fragment_navigationFooter_home)
                }
                response.onFailure {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // ao clicar em atualizar email
        binding.buttonProfileUpdateEmail.setOnClickListener {
            val email = binding.editTextProfileEmail.text.toString()

            viewModel.editUserEmailFromFirebase(email) { response ->
                // ir para o ecrã principal
                response.onSuccess {
                    findNavController().navigate(R.id.fragment_navigationFooter_home)
                }
                response.onFailure {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // ao clicar em atualizar palavra-passe
        binding.buttonProfileUpdatePassword.setOnClickListener {
            val newPassword = binding.editTextProfilePassword.text.toString()

            viewModel.editUserPasswordFromFirebase(newPassword) { response ->
                // ir para o ecrã principal
                response.onSuccess {
                    findNavController().navigate(R.id.fragment_navigationFooter_home)
                }
                response.onFailure {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // ao clicar no botão de terminar sessão, terminar sessão do utilizador e ir para a tela de login/registo
        binding.buttonProfileSignOut.setOnClickListener {
            viewModel.signOutFromFirebase()

            val intent = Intent(requireContext(), AuthActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        // ao clicar em apagar conta do utilizador
        binding.buttonProfileRemove.setOnClickListener {
            viewModel.removeUserFromFirebase { response ->
                // ir para o ecrã de login e registo
                response.onSuccess {
                    val intent = Intent(requireContext(), AuthActivity::class.java)
                    startActivity(intent)
                }
                response.onFailure {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}