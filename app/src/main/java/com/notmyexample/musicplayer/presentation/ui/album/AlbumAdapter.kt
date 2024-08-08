package com.notmyexample.musicplayer.presentation.ui.album

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.notmyexample.musicplayer.data.model.Album
import com.notmyexample.musicplayer.databinding.ItemAlbumBinding
import com.notmyexample.musicplayer.utils.formatTime

@SuppressLint("SetTextI18n")
class AlbumAdapter(
    private val onClick: (Album) -> Unit = {},
    private val getAlbumSongs: (Long) -> List<String> = { listOf() }
) : Adapter<AlbumAdapter.AlbumViewHolder>() {

    private val albums = mutableListOf<Album>()

    inner class AlbumViewHolder(val binding: ItemAlbumBinding) : ViewHolder(binding.root) {

        fun onBind(item: Album) {
            binding.tvName.text = item.name
            binding.tvDuration.text = formatTime(item.totalDuration)
            binding.tvSongs.text = "${getAlbumSongs(item.id)}"
            Glide.with(binding.ivThumbnail.context).load(item.thumbnail).into(binding.ivThumbnail)
        }

        fun handleEvent(item: Album) {
            binding.lnlAlbum.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding =
            ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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