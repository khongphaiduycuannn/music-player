package com.notmyexample.musicplayer.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.notmyexample.musicplayer.data.model.Album
import com.notmyexample.musicplayer.databinding.ItemAlbumBinding
import com.notmyexample.musicplayer.utils.darkenColor
import com.notmyexample.musicplayer.utils.getDominantColor
import com.notmyexample.musicplayer.utils.lighterColor

class AlbumAdapter(
    val onClick: (Album) -> Unit = {}
) : Adapter<AlbumAdapter.AlbumViewHolder>() {

    private val albums = mutableListOf<Album>()

    inner class AlbumViewHolder(val binding: ItemAlbumBinding) : ViewHolder(binding.root) {

        fun onBind(item: Album) {
            binding.tvAlbumName.text = item.name
            binding.tvAuthor.text = item.author
            Glide.with(binding.ivThumbnail.context).load(item.thumbnail).into(binding.ivThumbnail)

            item.thumbnail?.let {
                val color = getDominantColor(it)
                binding.ivTopBackground.setBackgroundColor(darkenColor(color))
                binding.ivBottomBackground.setBackgroundColor(lighterColor(color))
            }
        }

        fun handleEvent(item: Album) {
            binding.lnlAlbum.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlbumViewHolder(binding)
    }

    override fun getItemCount(): Int = albums.size

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.onBind(albums[position])
        holder.handleEvent(albums[position])
    }

    fun setAlbums(newList: MutableList<Album>) {
        albums.clear()
        newList.forEach { albums.add(it) }
        notifyDataSetChanged()
    }
}