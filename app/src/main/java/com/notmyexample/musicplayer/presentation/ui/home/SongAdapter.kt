package com.notmyexample.musicplayer.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.notmyexample.musicplayer.data.model.Song
import com.notmyexample.musicplayer.databinding.ItemSongBinding
import com.notmyexample.musicplayer.utils.formatTime

class SongAdapter(
    val onClick: (Song) -> Unit = {}
) : Adapter<SongAdapter.SongViewHolder>() {

    private val songs = mutableListOf<Song>()

    inner class SongViewHolder(val binding: ItemSongBinding) : ViewHolder(binding.root) {

        fun onBind(item: Song) {
            binding.tvName.text = item.name
            binding.tvAuthor.text = item.author
            binding.tvDuration.text = formatTime(item.duration)
            Glide.with(binding.ivThumbnail.context).load(item.thumbnail).into(binding.ivThumbnail)
        }

        fun handleEvent(item: Song) {
            binding.lnlSong.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun getItemCount(): Int = songs.size

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.onBind(songs[position])
        holder.handleEvent(songs[position])
    }

    fun setSongs(newList: MutableList<Song>) {
        songs.clear()
        newList.forEach { songs.add(it) }
        notifyDataSetChanged()
    }
}