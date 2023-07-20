package com.ipca.travelmemories.views.trip_create

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ipca.travelmemories.R
import com.ipca.travelmemories.databinding.FragmentTripCreateBinding
import com.ipca.travelmemories.views.home.HomeFragment
import java.io.File
import java.io.IOException
import java.util.*

class TripCreateFragment : Fragment() {
    private var _binding: FragmentTripCreateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TripCreateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // dados dos selecionadores de datas
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // ao clicar no botão de selecionar data de início
        binding.buttonTripCreateStartDate.setOnClickListener {
            DatePickerDialog(
                this@TripCreateFragment.requireContext(),
                // ao selecionar data
                { view, y, m, d ->
                    binding.textViewTripCreateStartDate.text = "$d-${m + 1}-$y"
                },
                year,
                month,
                day
            ).show()
        }

        // ao clicar no botão de selecionar data de fim
        binding.buttonTripCreateEndDate.setOnClickListener {
            DatePickerDialog(
                this@TripCreateFragment.requireContext(),
                // ao selecionar data
                { view, y, m, d ->
                    binding.textViewTripCreateEndDate.text = "$d-${m + 1}-$y"
                },
                year,
                month,
                day
            ).show()
        }

        // ao clicar no botão de escolher uma foto de capa
        binding.imageViewTripCreateCover.setOnClickListener {
            openCameraAndTakePhoto()
        }

        // ao clicar no botão de criar viagem
        binding.buttonTripCreateSave.setOnClickListener {
            val country = binding.editTextTripCreateCountry.text.toString()
            val cities = binding.editTextTripCreateCities.text.toString()
            val startDate = binding.textViewTripCreateStartDate.text.toString()
            val endDate = binding.textViewTripCreateEndDate.text.toString()

            // adicionar viagem na base de dados
            viewModel.addTripToFirebase(country, cities, startDate, endDate) { response ->
                response.onSuccess {
                    // guardar foto no storage
                    viewModel.uploadFileToFirebase { pathInDevice ->
                        pathInDevice?.let {
                            // ir para a tela das viagens e enviar os dados da viagem criada
                            response.onSuccess {
                                val bundle = Bundle()
                                bundle.putSerializable(HomeFragment.EXTRA_TRIP_CREATED, it)
                                findNavController().navigate(
                                    R.id.action_tripCreate_to_home,
                                    bundle
                                )
                            }
                        }
                        // mostrar erro
                        response.onFailure {
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                // mostrar erro
                response.onFailure {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // quando é tirada uma foto com sucesso apartir da câmara
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            // mostrar foto original
            BitmapFactory.decodeFile(viewModel.pathInDevice).apply {
                // necessário rodar a foto para ser mostrada com a orientação correta
                val matrix = Matrix()
                matrix.postRotate(90F)
                val rotatedBitmap =
                    Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)

                binding.imageViewTripCreateCover.setImageBitmap(rotatedBitmap)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openCameraAndTakePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    viewModel.createImageFile(requireContext())
                } catch (ex: IOException) {
                    Toast.makeText(context, "Error ao criar ficheiro da foto.", Toast.LENGTH_SHORT)
                        .show()
                    null
                }

                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.ipca.travelmemories.fileprovider",
                        it
                    )

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(
                        takePictureIntent,
                        REQUEST_TAKE_PHOTO
                    )
                }
            }
        }
    }

    companion object {
        const val REQUEST_TAKE_PHOTO = 1
    }
}