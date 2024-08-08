package com.notmyexample.musicplayer.presentation.ui.favourite

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.notmyexample.musicplayer.databinding.BottomSheetFragmentSortSongBinding
import com.notmyexample.musicplayer.utils.constant.SortTypeConstant.AUTHOR
import com.notmyexample.musicplayer.utils.constant.SortTypeConstant.DEFAULT
import com.notmyexample.musicplayer.utils.constant.SortTypeConstant.NAME

class SortSongBottomSheetDialog(
    val onSortTypeSelected: (String) -> Unit = {}
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFragmentSortSongBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetFragmentSortSongBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.ivClose.setOnClickListener { dismiss() }

        binding.lnlDefaultSort.setOnClickListener {
            onSortTypeSelected(DEFAULT)
            updateButtonView(DEFAULT)
        }

        binding.lnlNameSort.setOnClickListener {
            onSortTypeSelected(NAME)
            updateButtonView(NAME)
        }

        binding.lnlAuthorSort.setOnClickListener {
            onSortTypeSelected(AUTHOR)
            updateButtonView(AUTHOR)
        }
    }

    private fun updateButtonView(sortType: String) {
        binding.tvDefaultSort.typeface = Typeface.DEFAULT
        binding.tvNameSort.typeface = Typeface.DEFAULT
        binding.tvAuthorSort.typeface =  Typeface.DEFAULT

        binding.cbDefaultSort.isChecked = false
        binding.cbNameSort.isChecked = false
        binding.cbAuthorSort.isChecked = false

        when (sortType) {
            DEFAULT -> {
                binding.tvDefaultSort.typeface = Typeface.DEFAULT_BOLD
                binding.cbDefaultSort.isChecked = true
            }

            NAME -> {
                binding.tvNameSort.typeface = Typeface.DEFAULT_BOLD
                binding.cbNameSort.isChecked = true
            }

            AUTHOR -> {
                binding.tvAuthorSort.typeface =  Typeface.DEFAULT_BOLD
                binding.cbAuthorSort.isChecked = true
            }
        }
    }
}