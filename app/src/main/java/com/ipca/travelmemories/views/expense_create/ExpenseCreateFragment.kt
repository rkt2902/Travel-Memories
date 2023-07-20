package com.ipca.travelmemories.views.expense_create

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ipca.travelmemories.R
import com.ipca.travelmemories.databinding.FragmentExpenseCreateBinding
import com.ipca.travelmemories.views.expense_all.ExpenseAllFragment
import java.util.*

class ExpenseCreateFragment : Fragment() {
    private var _binding: FragmentExpenseCreateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExpenseCreateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // dados do selecionador da data da despesa
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // definir itens da dropdown da categoria da despesa
        binding.spinnerExpenseCreateCategory
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.expense_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerExpenseCreateCategory.adapter = adapter
        }

        // ao clicar no botão de selecionar data de início
        binding.buttonExpenseCreateDate.setOnClickListener {
            DatePickerDialog(
                this@ExpenseCreateFragment.requireContext(),

                // ao selecionar data
                { view, y, m, d ->
                    binding.textViewExpenseCreateDate.text = "$d-${m + 1}-$y"
                },
                year,
                month,
                day
            ).show()
        }

        // ao clicar no botão de criar despesa
        binding.buttonExpenseCreateSave.setOnClickListener {
            // definir viagem
            val tripID = getSharedTripID()
            val category = binding.spinnerExpenseCreateCategory.selectedItem.toString()
            val price = binding.editTextExpenseCreatePrice.text.toString().toDouble()
            val description = binding.editTextExpenseCreateDescription.text.toString()
            val date = binding.textViewExpenseCreateDate.text.toString()

            // adicionar despesa na base de dados
            viewModel.addExpensesToFirebase(
                tripID!!,
                category,
                price,
                description,
                date
            ) { response ->
                // ir para a tela das despesas e enviar os dados da despesa criada
                response.onSuccess {
                    val bundle = Bundle()
                    bundle.putSerializable(ExpenseAllFragment.EXTRA_EXPENSE_CREATED, it)
                    findNavController().navigate(
                        R.id.action_expenseCreate_to_expenseAll,
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

    private fun getSharedTripID(): String? {
        val sharedPreference = activity?.getPreferences(Context.MODE_PRIVATE) ?: return ""
        return sharedPreference.getString(getString(R.string.shared_trip_id), "")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}