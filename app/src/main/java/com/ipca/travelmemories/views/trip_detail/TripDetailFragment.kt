package com.ipca.travelmemories.views.trip_detail

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ipca.travelmemories.R
import com.ipca.travelmemories.databinding.FragmentTripDetailBinding
import com.ipca.travelmemories.models.TripModel
import com.ipca.travelmemories.utils.ParserUtil
import java.util.*

class TripDetailFragment : Fragment() {
    private var _binding: FragmentTripDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TripDetailViewModel by viewModels()

    private lateinit var trip: TripModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            trip = it.getSerializable(EXTRA_TRIP_DETAILS) as TripModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextTripDetailCountry.setText(trip.country)
        binding.editTextTripDetailCities.setText(trip.cities)
        binding.textViewTripDetailStartDate.setText(
            ParserUtil.convertDateToString(
                trip.startDate!!,
                "dd-MM-yyyy"
            )
        )
        binding.textViewTripDetailEndDate.setText(
            ParserUtil.convertDateToString(
                trip.endDate!!,
                "dd-MM-yyyy"
            )
        )

        // fazer download da foto e visualizá-la na imageView
        trip.coverPath?.let { filePath ->
            viewModel.getPhotoURI(filePath) { response ->
                response!!.onSuccess { uri ->
                    Glide.with(requireContext())
                        .load(uri)
                        .into(binding.imageViewTripDetailCover)
                }
                response!!.onFailure {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // dados dos selecionadores de datas
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // ao clicar no botão de selecionar data de início
        binding.buttonTripDetailStartDate.setOnClickListener {
            DatePickerDialog(
                this@TripDetailFragment.requireContext(),
                // ao selecionar data
                { view, y, m, d ->
                    binding.textViewTripDetailStartDate.text = "$d-${m + 1}-$y"
                },
                year,
                month,
                day
            ).show()
        }

        // ao clicar no botão de selecionar data de fim
        binding.buttonTripDetailEndDate.setOnClickListener {
            DatePickerDialog(
                this@TripDetailFragment.requireContext(),
                // ao selecionar data
                { view, y, m, d ->
                    binding.textViewTripDetailEndDate.text = "$d-${m + 1}-$y"
                },
                year,
                month,
                day
            ).show()
        }

        // ao clicar no botão de ir para o ecrã das fotos
        binding.buttonTripDetailPhotos.setOnClickListener {
            findNavController().navigate(R.id.action_tripDetail_to_photoAll)
        }

        // ao clicar no botão de ir para o ecrã das despesas
        binding.buttonTripDetailExpenses.setOnClickListener {
            findNavController().navigate(R.id.action_tripDetail_to_expenseAll)
        }

        // ao clicar no botão de ir para o ecrã do diário
        binding.buttonTripDetailDiary.setOnClickListener {
            findNavController().navigate(R.id.action_tripDetail_to_diaryDayAll)
        }

        // ao clicar no botão de atualizar viagem
        binding.buttonTripDetailEdit.setOnClickListener {
            val tripID = trip.id
            val country = binding.editTextTripDetailCountry.text.toString()
            val cities = binding.editTextTripDetailCities.text.toString()
            val startDate = binding.textViewTripDetailStartDate.text.toString()
            val endDate = binding.textViewTripDetailEndDate.text.toString()

            viewModel.editTripFromFirebase(
                tripID!!,
                country,
                cities,
                startDate,
                endDate
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

        // ao clicar no botão de apagar viagem
        binding.buttonTripDetailRemove.setOnClickListener {
            trip.coverPath?.let { coverPath ->
                viewModel.removeTripFromFirebase(trip.id!!, coverPath) { response ->
                    // ir para a página principal
                    response.onSuccess {
                        findNavController().navigate(R.id.fragment_navigationFooter_home)
                    }
                    response.onFailure {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EXTRA_TRIP_DETAILS = "EXTRA_TRIP_DETAILS"
    }
}