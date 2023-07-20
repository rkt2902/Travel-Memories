package com.ipca.travelmemories.views.photo_create

import android.app.Activity.RESULT_OK
import android.content.Context
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
import com.ipca.travelmemories.databinding.FragmentPhotoCreateBinding
import com.ipca.travelmemories.views.home.HomeFragment
import java.io.File
import java.io.IOException

class PhotoCreateFragment : Fragment() {
    private var _binding: FragmentPhotoCreateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PhotoCreateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openCameraAndTakePhoto()

        // ao clicar em guardar foto
        binding.buttonPhotoCreateSave.setOnClickListener {
            val tripID = getSharedTripID()
            val description = binding.editTextPhotoCreateDescription.text.toString()

            viewModel.addPhotoToDatabase(tripID!!, description) { response ->
                response.onSuccess {
                    // guardar foto no storage
                    viewModel.uploadFileToFirebase { pathInDevice ->
                        pathInDevice?.let {
                            // ir para a tela das viagens e enviar os dados da viagem criada
                            response.onSuccess {
                                val bundle = Bundle()
                                bundle.putSerializable(HomeFragment.EXTRA_TRIP_CREATED, it)
                                findNavController().popBackStack()
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
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // mostrar foto original
            BitmapFactory.decodeFile(viewModel.pathInDevice).apply {
                // necessário rodar a foto para ser mostrada com a orientação correta
                val matrix = Matrix()
                matrix.postRotate(90F)
                val rotatedBitmap =
                    Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)

                binding.imageViewPhotoCreatePhoto.setImageBitmap(rotatedBitmap)
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
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    private fun getSharedTripID(): String? {
        val sharedPreference = activity?.getPreferences(Context.MODE_PRIVATE) ?: return ""
        return sharedPreference.getString(getString(R.string.shared_trip_id), "")
    }

    companion object {
        const val REQUEST_TAKE_PHOTO = 1
    }
}