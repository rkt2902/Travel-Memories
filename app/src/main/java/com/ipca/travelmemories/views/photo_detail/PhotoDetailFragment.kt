package com.ipca.travelmemories.views.photo_detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ipca.travelmemories.R
import com.ipca.travelmemories.databinding.FragmentPhotoDetailBinding
import com.ipca.travelmemories.models.PhotoModel

class PhotoDetailFragment : Fragment() {
    private var _binding: FragmentPhotoDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PhotoDetailViewModel by viewModels()

    private lateinit var photo: PhotoModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            photo = it.getSerializable(EXTRA_PHOTO_DETAILS) as PhotoModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewPhotoDetailDescription.text = photo.description

        // fazer download da foto e visualizá-la na imageView
        photo.filePath?.let { filePath ->
            viewModel.getPhotoURI(filePath) { response ->
                response!!.onSuccess { uri ->
                    Glide.with(requireContext())
                        .load(uri)
                        .into(binding.imageViewPhotoDetailPhoto)
                }
                response!!.onFailure {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // ao clicar no botão de apagar foto
        binding.buttonPhotoDetailRemove.setOnClickListener {
            val tripID = getSharedTripID()

            photo.filePath?.let { filePath ->
                viewModel.removePhotoFromFirebase(tripID!!, photo.id!!, filePath) { response ->
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
        const val EXTRA_PHOTO_DETAILS = "EXTRA_PHOTO_DETAILS"
    }
}