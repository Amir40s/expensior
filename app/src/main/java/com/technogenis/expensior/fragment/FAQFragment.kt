package com.technogenis.expensior.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.technogenis.expensior.R
import com.technogenis.expensior.databinding.FragmentFAQBinding

class FAQFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var binding : FragmentFAQBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFAQBinding.inflate(layoutInflater, container, false)

    return binding.root
    }


}