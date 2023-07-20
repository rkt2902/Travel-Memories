package com.ipca.travelmemories.views.diary_day_detail

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ipca.travelmemories.R
import com.ipca.travelmemories.databinding.FragmentDiaryDayDetailBinding
import com.ipca.travelmemories.models.DiaryDayModel
import com.ipca.travelmemories.utils.ParserUtil
import java.util.*

class DiaryDayDetailFragment : Fragment() {
    private var _binding: FragmentDiaryDayDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiaryDayDetailViewModel by viewModels()

    private lateinit var diaryDay: DiaryDayModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            diaryDay =
                it.getSerializable(EXTRA_DIARY_DAY_DETAILS) as DiaryDayModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryDayDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextDiaryDayDetailTitle.setText(diaryDay.title)
        binding.editTextDiaryDayDetailBody.setText(diaryDay.body)
        binding.textViewDiaryDayDetailDate.text =
            ParserUtil.convertDateToString(diaryDay.date!!, "dd-MM-yyyy")

        // dados do selecionador da data
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // ao clicar no botão de selecionar data do dia
        binding.buttonDiaryDayDetailDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                // ao selecionar data
                { view, y, m, d ->
                    binding.textViewDiaryDayDetailDate.text = "$d-${m + 1}-$y"
                },
                year,
                month,
                day
            ).show()
        }

        // ao clicar no botão de atualizar dia do diário
        binding.buttonDiaryDayDetailUpdate.setOnClickListener {
            val tripID = getSharedTripID()
            val title = binding.editTextDiaryDayDetailTitle.text.toString()
            val body = binding.editTextDiaryDayDetailBody.text.toString()
            val date = binding.textViewDiaryDayDetailDate.text.toString()

            viewModel.editDiaryDayFromFirebase(
                tripID!!,
                diaryDay.id!!,
                title,
                body,
                date
            ) { response ->
                // voltar à página da lista de fotos
                response.onSuccess {
                    findNavController().popBackStack()
                }
                response.onFailure {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // ao clicar no botão de apagar dia do diário
        binding.buttonDiaryDayDetailRemove.setOnClickListener {
            val tripID = getSharedTripID()

            viewModel.removeDiaryDayFromFirebase(tripID!!, diaryDay.id!!) { response ->
                // voltar à página da lista de fotos
                response.onSuccess {
                    findNavController().popBackStack()
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

    private fun getSharedTripID(): String? {
        val sharedPreference = activity?.getPreferences(Context.MODE_PRIVATE) ?: return ""
        return sharedPreference.getString(getString(R.string.shared_trip_id), "")
    }

    companion object {
        const val EXTRA_DIARY_DAY_DETAILS = "EXTRA_DIARY_DAY_DETAILS"
    }
}