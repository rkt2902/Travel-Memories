package com.ipca.travelmemories.views.expense_detail

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ipca.travelmemories.R
import com.ipca.travelmemories.databinding.FragmentExpenseDetailBinding
import com.ipca.travelmemories.models.ExpenseModel
import com.ipca.travelmemories.utils.ParserUtil
import java.util.*


class ExpenseDetailFragment : Fragment() {
    private var _binding: FragmentExpenseDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExpenseDetailViewModel by viewModels()

    private lateinit var expense: ExpenseModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            expense =
                it.getSerializable(EXTRA_EXPENSE_DETAILS) as ExpenseModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // definir itens da dropdown da categoria da despesa
        binding.spinnerExpenseDetailCategory
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.expense_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerExpenseDetailCategory.adapter = adapter
        }

        binding.spinnerExpenseDetailCategory.setSelection(
            getCurrentSpinnerIndex(
                binding.spinnerExpenseDetailCategory,
                expense.category.toString()
            )
        )
        binding.editTextExpenseDetailPrice.setText(expense.price.toString())
        binding.editTextExpenseDetailDescription.setText(expense.description)
        binding.textViewExpenseDetailDate.text =
            ParserUtil.convertDateToString(expense.date!!, "dd-MM-yyyy")

        // dados do selecionador da data
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // ao clicar no botão de selecionar data do dia
        binding.buttonExpenseDetailDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                // ao selecionar data
                { view, y, m, d ->
                    binding.textViewExpenseDetailDate.text = "$d-${m + 1}-$y"
                },
                year,
                month,
                day
            ).show()
        }

        // ao clicar no botão de atualizar dia do diário
        binding.buttonExpenseDetailUpdate.setOnClickListener {
            val tripID = getSharedTripID()
            val category = binding.spinnerExpenseDetailCategory.selectedItem.toString()
            val price = binding.editTextExpenseDetailPrice.text.toString().toDouble()
            val description = binding.editTextExpenseDetailDescription.text.toString()
            val date = binding.textViewExpenseDetailDate.text.toString()

            viewModel.editExpenseFromFirebase(
                tripID!!,
                expense.id!!,
                category,
                price,
                description,
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
        binding.buttonExpenseDetailRemove.setOnClickListener {
            val tripID = getSharedTripID()

            viewModel.removeExpenseFromFirebase(tripID!!, expense.id!!) { response ->
                // voltar à página da lista de despesas
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

    private fun getCurrentSpinnerIndex(spinner: Spinner, myString: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)) {
                return i
            }
        }
        return 0
    }

    private fun getSharedTripID(): String? {
        val sharedPreference = activity?.getPreferences(Context.MODE_PRIVATE) ?: return ""
        return sharedPreference.getString(getString(R.string.shared_trip_id), "")
    }

    companion object {
        const val EXTRA_EXPENSE_DETAILS = "EXTRA_EXPENSE_DETAILS"
    }
}