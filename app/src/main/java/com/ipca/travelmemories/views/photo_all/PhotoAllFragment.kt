package com.ipca.travelmemories.views.photo_all

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ipca.travelmemories.R
import com.ipca.travelmemories.databinding.FragmentPhotoAllBinding
import com.ipca.travelmemories.models.PhotoModel
import com.ipca.travelmemories.views.photo_detail.PhotoDetailFragment

class PhotoAllFragment : Fragment() {
    private var _binding: FragmentPhotoAllBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PhotoAllViewModel by viewModels()

    private var photos = arrayListOf<PhotoModel>()
    private val adapter = PhotosAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // atualizar view com a lista das fotos
        val tripID = getSharedTripID()

        viewModel.getPhotosDataFromFirebase(tripID!!).observe(viewLifecycleOwner) { response ->
            response.onSuccess {
                photos = it as ArrayList
                adapter.notifyDataSetChanged()
            }
            response.onFailure {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.listViewPhotoAllPhotos.adapter = adapter

        // ao clicar no botão de adicionar foto. ir para o ecrã de abrir a câmara
        binding.buttonPhotoAllAddPhoto.setOnClickListener {
            findNavController().navigate(R.id.action_photoAll_to_photoCreate)
        }
    }

    inner class PhotosAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return photos.size
        }

        override fun getItem(position: Int): Any {
            return photos[position]
        }

        override fun getItemId(position: Int): Long {
            return 0L
        }

        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            val rootView = layoutInflater.inflate(R.layout.row_photo, parent, false)

            val textViewDescription =
                rootView.findViewById<TextView>(R.id.textView_photoAll_description)
            textViewDescription.text = photos[position].description

            // fazer download da foto e visualizá-la na imageView
            val imageViewPhoto = rootView.findViewById<ImageView>(R.id.imageView_photoAll_photo)
            viewModel.getPhotoURI(photos[position].filePath!!) { response ->
                response!!.onSuccess { uri ->
                    Glide.with(requireContext())
                        .load(uri)
                        .into(imageViewPhoto)
                }
                response!!.onFailure {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }

            // ao clicar numa foto, ir para o ecrã individual da foto e enviar os dados dessa foto
            rootView.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable(PhotoDetailFragment.EXTRA_PHOTO_DETAILS, photos[position])
                findNavController().navigate(R.id.action_photoAll_to_photoDetail, bundle)
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
}