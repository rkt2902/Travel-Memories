package com.ipca.travelmemories.views.diary_day_create

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ipca.travelmemories.R
import com.ipca.travelmemories.databinding.FragmentDiaryDayCreateBinding
import com.ipca.travelmemories.views.diary_day_all.DiaryDayAllFragment
import java.util.*

class DiaryDayCreateFragment : Fragment() {
    private var _binding: FragmentDiaryDayCreateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiaryDayCreateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryDayCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // dados do selecionador da data
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // ao clicar no botão de selecionar data do dia
        binding.buttonDiaryDayCreateDate.setOnClickListener {
            DatePickerDialog(
                this@DiaryDayCreateFragment.requireContext(),
                // ao selecionar data
                { view, y, m, d ->
                    binding.textViewDiaryDayCreateDate.text = "$d-${m + 1}-$y"
                },
                year,
                month,
                day
            ).show()
        }

        // ao clicar no botão de criar diário
        binding.buttonDiaryDayCreateSave.setOnClickListener {
            val tripID = getSharedTripID()
            val title = binding.editTextDiaryDayCreateTitle.text.toString()
            val body = binding.editTextDiaryDayCreateBody.text.toString()
            val date = binding.textViewDiaryDayCreateDate.text.toString()

            // adicionar viagem na base de dados
            viewModel.addDiaryDayToFirebase(tripID!!, title, body, date) { response ->
                // ir para a tela do diário e enviar os dados do dia criado
                response.onSuccess {
                    val bundle = Bundle()
                    bundle.putSerializable(DiaryDayAllFragment.EXTRA_DIARY_DAY_CREATED, it)
                    findNavController().navigate(
                        R.id.action_diaryDayCreate_to_diaryDayAll,
                        bundle
                    )
                }
                // mostrar erro
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

    private fun getSharedTripID(): String? {
        val sharedPreference = activity?.getPreferences(Context.MODE_PRIVATE) ?: return ""
        return sharedPreference.getString(getString(R.string.shared_trip_id), "")
    }
}