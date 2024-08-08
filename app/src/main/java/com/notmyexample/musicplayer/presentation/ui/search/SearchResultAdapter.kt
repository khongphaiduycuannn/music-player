package com.notmyexample.musicplayer.presentation.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.notmyexample.musicplayer.data.model.Song
import com.notmyexample.musicplayer.databinding.ItemSearchResultBinding

class SearchResultAdapter(
    private val onClick: (Song) -> Unit = {}
) : Adapter<SearchResultAdapter.SearchResultViewHolder>() {

    private val songs = mutableListOf<Song>()

    inner class SearchResultViewHolder(val binding: ItemSearchResultBinding) :
        ViewHolder(binding.root) {

        fun onBind(song: Song) {
            binding.tvName.text = song.name
            binding.tvAuthor.text = song.author
            Glide.with(binding.ivThumbnail.context).load(song.thumbnail).into(binding.ivThumbnail)
            if (song.isFavorite) {
                binding.ivIsFavourite.visibility = View.VISIBLE
            } else {
                binding.ivIsFavourite.visibility = View.GONE
            }
        }

        fun handleEvent(song: Song) {
            binding.lnlSong.setOnClickListener { onClick(song) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding =
            ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultViewHolder(binding)
    }

    override fun getItemCount(): Int = songs.size

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.onBind(songs[position])
        holder.handleEvent(songs[position])
    }

    fun setSongs(newList: MutableList<Song>) {
        val songsSize = songs.size
        songs.clear()
        newList.forEach { songs.add(it) }

        if (newList.size == 0) {
            notifyItemRangeRemoved(0, songsSize)
        }
        else {
            notifyDataSetChanged()
        }
    }
}