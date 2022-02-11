package com.adurandet.bereal.browsing.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.adurandet.bereal.BuildConfig
import com.adurandet.bereal.common.network.ApiHelper.authorizaztionHeader
import com.adurandet.bereal.databinding.ImageFramentBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl

class ImageFragment : Fragment() {

    private lateinit var binding: ImageFramentBinding

    private val imageFragmentArgs: ImageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ImageFramentBinding.inflate(inflater)

        val url = buildImageUrl()
        val glideURL = GlideUrl(url) { mapOf(authorizaztionHeader)}
        Glide.with(binding.root.context)
            .load(glideURL)
            .fitCenter()
            .into(binding.image)

        return binding.root

    }

    private fun buildImageUrl() = "${BuildConfig.BASE_URL}items/${imageFragmentArgs.id}/data"
}