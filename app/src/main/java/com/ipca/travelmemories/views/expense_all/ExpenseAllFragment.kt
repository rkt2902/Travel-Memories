package com.ipca.travelmemories.views.expense_all

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ipca.travelmemories.R
import com.ipca.travelmemories.databinding.FragmentExpenseAllBinding
import com.ipca.travelmemories.models.ExpenseModel
import com.ipca.travelmemories.utils.ParserUtil
import com.ipca.travelmemories.views.expense_detail.ExpenseDetailFragment

class ExpenseAllFragment : Fragment() {
    private var _binding: FragmentExpenseAllBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExpenseAllViewModel by viewModels()

    private var expenses = arrayListOf<ExpenseModel>()
    private val adapter = ExpenseAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // atualizar view com a lista das despesas
        val tripID = getSharedTripID()

        viewModel.getExpensesFromFirebase(tripID!!).observe(viewLifecycleOwner) { response ->
            response.onSuccess {
                expenses = it as ArrayList<ExpenseModel>
                adapter.notifyDataSetChanged()
            }
            response.onFailure {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.listViewExpenseAllExpenses.adapter = adapter

        // ao clicar no botão de adicionar despesa
        binding.buttonExpenseAllAddExpense.setOnClickListener {
            // ir para a tela de adicionar despesa
            findNavController().navigate(R.id.action_expenseAll_to_expenseCreate)
        }
    }

    inner class ExpenseAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return expenses.size
        }

        override fun getItem(position: Int): Any {
            return expenses[position]
        }

        override fun getItemId(position: Int): Long {
            return 0L
        }

        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            val rootView = layoutInflater.inflate(R.layout.row_expense, parent, false)

            val textViewCat = rootView.findViewById<TextView>(R.id.textView_expenseAll_cat)
            textViewCat.text = expenses[position].category

            val textViewPrice = rootView.findViewById<TextView>(R.id.textView_expenseAll_price)
            textViewPrice.text = expenses[position].price.toString()

            val textViewDate = rootView.findViewById<TextView>(R.id.textView_expenseAll_Data)
            textViewDate.text = ParserUtil.convertDateToString(expenses[position].date!!, "dd-MM-yyyy")


            // ao clicar num dia do diário, ir para o ecrã individual do dia e enviar os dados desse dia
            rootView.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable(
                    ExpenseDetailFragment.EXTRA_EXPENSE_DETAILS,
                    expenses[position]
                )
                findNavController().navigate(R.id.action_expenseAll_to_expenseDetail, bundle)
            }

            return rootView
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
        const val EXTRA_EXPENSE_CREATED = "EXTRA_EXPENSE_CREATED"
    }
}