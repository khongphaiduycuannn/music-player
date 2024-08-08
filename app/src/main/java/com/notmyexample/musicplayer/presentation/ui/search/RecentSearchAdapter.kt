package com.notmyexample.musicplayer.presentation.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.notmyexample.musicplayer.data.model.Song
import com.notmyexample.musicplayer.databinding.ItemRecentSearchBinding

class RecentSearchAdapter(
    private val onClick: (Song) -> Unit = {},
    private val onDelete: (Song) -> Unit = {}
) : Adapter<RecentSearchAdapter.RecentSearchViewHolder>() {

    private val songs = mutableListOf<Song>()

    inner class RecentSearchViewHolder(val binding: ItemRecentSearchBinding) :
        ViewHolder(binding.root) {

        fun onBind(song: Song) {
            binding.tvName.text = song.name
            binding.tvAuthor.text = song.author
            Glide.with(binding.ivThumbnail.context).load(song.thumbnail).into(binding.ivThumbnail)
        }

        fun handleEvent(song: Song) {
            binding.lnlSong.setOnClickListener { onClick(song) }
            binding.ivDelete.setOnClickListener { onDelete(song) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {
        val binding =
            ItemRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentSearchViewHolder(binding)
    }

    override fun getItemCount(): Int = songs.size

    override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
        holder.onBind(songs[position])
        holder.handleEvent(songs[position])
    }

    fun setSongs(newList: MutableList<Song>) {
        val list = songs.toMutableList()
        songs.clear()
        newList.forEach { songs.add(it) }
        notifyDataSetChange(list, newList)
    }

    private fun notifyDataSetChange(list: MutableList<Song>, newList: MutableList<Song>) {
        val diff = newList.size - list.size
        when (diff) {
            0 -> {}

            1 -> notifyItemInserted(newList.size - 1)

            -1 -> {
                newList.forEachIndexed { index, song ->
                    if (song.id != list[index].id) {
                        notifyItemRemoved(index)
                        return@forEachIndexed
                    }
                }
                notifyItemRemoved(list.size - 1)
            }

            else -> notifyDataSetChanged()
        }
    }
}