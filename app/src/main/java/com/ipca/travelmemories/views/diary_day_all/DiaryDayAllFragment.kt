package com.ipca.travelmemories.views.diary_day_all

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
import com.ipca.travelmemories.databinding.FragmentDiaryDayAllBinding
import com.ipca.travelmemories.models.DiaryDayModel
import com.ipca.travelmemories.views.diary_day_detail.DiaryDayDetailFragment
import com.ipca.travelmemories.utils.ParserUtil

class DiaryDayAllFragment : Fragment() {
    private var _binding: FragmentDiaryDayAllBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiaryDayAllViewModel by viewModels()

    private var diaryDays = arrayListOf<DiaryDayModel>()
    private val adapter = DiaryDaysAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryDayAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // atualizar view com a lista dos dias do diário
        val tripID = getSharedTripID()

        viewModel.getDiaryDaysFromFirebase(tripID!!).observe(viewLifecycleOwner) { response ->
            response.onSuccess {
                diaryDays = it as ArrayList<DiaryDayModel>
                adapter.notifyDataSetChanged()
            }
            response.onFailure {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.listViewDiaryDayAllDiaryDays.adapter = adapter

        // ao clicar no botão de adicionar dia ao diário
        binding.buttonDiatyDayAllAddDays.setOnClickListener {
            // ir para a tela de adicionar dia ao diário e enviar o ID da viagem
            findNavController().navigate(R.id.action_diaryDayAll_to_diaryDayCreate)
        }
    }

    inner class DiaryDaysAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return diaryDays.size
        }

        override fun getItem(position: Int): Any {
            return diaryDays[position]
        }

        override fun getItemId(position: Int): Long {
            return 0L
        }

        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            val rootView = layoutInflater.inflate(R.layout.row_diary, parent, false)

            val textViewTitle = rootView.findViewById<TextView>(R.id.textView_diaryAll_title)
            textViewTitle.text = diaryDays[position].title

            val textViewDate = rootView.findViewById<TextView>(R.id.textView_diaryAll_Date)
            textViewDate.text = ParserUtil.convertDateToString(diaryDays[position].date!!, "dd-MM-yyyy")


            // ao clicar num dia do diário, ir para o ecrã individual do dia e enviar os dados desse dia
            rootView.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable(
                    DiaryDayDetailFragment.EXTRA_DIARY_DAY_DETAILS,
                    diaryDays[position]
                )
                findNavController().navigate(R.id.action_diaryDayAll_to_diaryDayDetail, bundle)
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
        const val EXTRA_DIARY_DAY_CREATED = "EXTRA_DIARY_DAY_CREATED"
    }
}