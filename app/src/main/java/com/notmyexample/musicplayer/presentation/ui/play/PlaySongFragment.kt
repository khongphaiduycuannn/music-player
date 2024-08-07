package com.notmyexample.musicplayer.presentation.ui.play

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.notmyexample.musicplayer.R
import com.notmyexample.musicplayer.data.model.Playlist
import com.notmyexample.musicplayer.data.model.Song
import com.notmyexample.musicplayer.databinding.FragmentPlaySongBinding
import com.notmyexample.musicplayer.presentation.MainActivity
import com.notmyexample.musicplayer.presentation.custom_view.animation.ResizeHeightAnimation
import com.notmyexample.musicplayer.presentation.navigator.AppNavigator
import com.notmyexample.musicplayer.presentation.navigator.Screens
import com.notmyexample.musicplayer.presentation.service.PlaySongManager
import com.notmyexample.musicplayer.presentation.ui.home.SongAdapter
import com.notmyexample.musicplayer.utils.constant.PlayEventEConstant.EVENT_PAUSE
import com.notmyexample.musicplayer.utils.constant.PlayEventEConstant.EVENT_PLAY
import com.notmyexample.musicplayer.utils.formatTime
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.max

private const val TAG = "PlaySongFragment"

@AndroidEntryPoint
class PlaySongFragment : Fragment() {

    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var playSongManager: PlaySongManager

    private val binding by lazy { FragmentPlaySongBinding.inflate(layoutInflater) }

    private val viewModel: PlaySongViewModel by viewModels()

    private val onSongItemClick: (Song) -> Unit = {
        playSongManager.playNewSong(it)
        (activity as MainActivity).startPlaySongService(EVENT_PLAY)
    }

    private val songAdapter = SongAdapter(onSongItemClick)

    private var rotateAnimator: ObjectAnimator? = null
    private var buttonCollapseRotateAnimator: ObjectAnimator? = null
    private var resizeLayoutAnimation: ResizeHeightAnimation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as MainActivity).updateBottomBar(Screens.PLAY)

        initView()
        handleEvent()
        observeData()
    }

    private fun initView() {
        binding.rclSongList.adapter = songAdapter
        binding.rclSongList.layoutManager = LinearLayoutManager(requireContext())

        binding.constrainLayout.doOnPreDraw {
            viewModel.actionBarWidth = max(it.width.toFloat(), viewModel.actionBarWidth)
        }

        if (viewModel.isCollapse) {
            binding.constrainLayout.visibility = View.GONE
            startCollapseButtonRotateAnim(0f)
        } else {
            binding.constrainLayout.visibility = View.VISIBLE
            startCollapseButtonRotateAnim(180f)
        }
    }

    private fun handleEvent() {
        binding.ivBack.setOnClickListener { appNavigator.popBackStack() }

        binding.lnShowPlaylist.setOnClickListener { appNavigator.navigateTo(Screens.SONGS) }

        binding.lnSongName.setOnClickListener(onCollapseButtonClick)

        binding.ivPlayOrPause.setOnClickListener(onPlayButtonClick)

        binding.ivNextSong.setOnClickListener { playSongManager.playNextSong() }

        binding.ivPreviousSong.setOnClickListener { playSongManager.playPreviousSong() }

        binding.ivLoop.setOnClickListener(onLoopButtonClick)

        binding.ivNext5Second.setOnClickListener { playSongManager.skipNextFiveSeconds() }

        binding.ivPrevious5Second.setOnClickListener { playSongManager.skipPreviousFiveSeconds() }

        binding.sbProgress.setOnSeekBarChangeListener(onSeekBarChangeListener)

        binding.ivFavourite.setOnClickListener { playSongManager.favouriteCurrentSong() }

        binding.ivInformation.setOnClickListener { Log.d(TAG, "handleEvent: ${playSongManager.songsLiveData.value}") }
    }

    private fun observeData() {
        with(playSongManager) {
            currentPlayLiveData.observe(viewLifecycleOwner) { setSongDataView(it) }

            playlistLiveData.observe(viewLifecycleOwner) {
                songAdapter.setSongs(it.songs.take(3).toMutableList())
            }

            currentPositionLiveData.observe(viewLifecycleOwner) {
                binding.tvCurrentTime.text = formatTime(it)
                binding.sbProgress.progress = it
            }

            isPlayingLiveData.observe(viewLifecycleOwner) { setIsPlayingButtonView(it) }

            isLoopingLiveData.observe(viewLifecycleOwner) {
                if (it) {
                    binding.ivLoop.setImageResource(R.drawable.ic_loop_colored)
                } else {
                    binding.ivLoop.setImageResource(R.drawable.ic_loop)
                }
            }
        }
    }

    private val onCollapseButtonClick = View.OnClickListener {
        if (viewModel.isCollapse) {
            startResizeLayoutAnim(1f, viewModel.actionBarWidth, false)
            startCollapseButtonRotateAnim(180f)
        } else {
            startCollapseButtonRotateAnim(0f)
            startResizeLayoutAnim(viewModel.actionBarWidth, 1f, true)
        }
        viewModel.isCollapse = !viewModel.isCollapse
    }

    private val onPlayButtonClick = View.OnClickListener {
        val isPlaying = playSongManager.isPlayingLiveData.value
        if (isPlaying == true) {
            playSongManager.pauseCurrentSong()
            (activity as MainActivity).startPlaySongService(EVENT_PAUSE)
        }
        else {
            playSongManager.resumeCurrentSong()
            (activity as MainActivity).startPlaySongService(EVENT_PLAY)
        }
    }

    private val onLoopButtonClick = View.OnClickListener {
        val isLooping = playSongManager.isLoopingLiveData.value
        if (isLooping == true) playSongManager.stopLoop()
        else playSongManager.startLoop()
    }

    private val onSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, positon: Int, fromUser: Boolean) {
            if (fromUser) {
                playSongManager.seekTo(positon)
            }
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {

        }

        override fun onStopTrackingTouch(p0: SeekBar?) {

        }
    }

    private fun setSongDataView(song: Song?) {
        song?.apply {
            binding.tvSongName.text = name
            binding.tvAuthorName.text = author
            binding.tvDuration.text = formatTime(duration)
            binding.sbProgress.max = duration
            Glide.with(requireContext()).load(thumbnail).into(binding.ivThumbnail)
            if (isFavorite) {
                binding.ivFavourite.setImageResource(R.drawable.ic_favourite_true)
            } else {
                binding.ivFavourite.setImageResource(R.drawable.ic_favourite)
            }
            songAdapter.setCurrentSong(song)
        }
    }

    private fun setIsPlayingButtonView(isPlaying: Boolean) {
        if (isPlaying) {
            binding.ivPlayOrPause.setImageResource(R.drawable.ic_pause_action)
            startRotateAnim()
        } else {
            binding.ivPlayOrPause.setImageResource(R.drawable.ic_play_action)
            rotateAnimator?.cancel()
        }
        songAdapter.setIsPlaying(isPlaying)
    }

    private fun startRotateAnim() {
        val startPosition = binding.ivThumbnail.rotation
        rotateAnimator?.cancel()
        rotateAnimator = ObjectAnimator.ofFloat(
            binding.ivThumbnail, View.ROTATION, startPosition, startPosition + 360f
        )
        rotateAnimator = rotateAnimator?.apply {
            duration = 30000
            repeatCount = Animation.INFINITE
            interpolator = LinearInterpolator()
        }
        rotateAnimator?.start()
    }

    private fun startResizeLayoutAnim(from: Float, to: Float, isHide: Boolean) {
        binding.constrainLayout.visibility = View.VISIBLE
        binding.constrainLayout.clearAnimation()

        resizeLayoutAnimation?.cancel()
        resizeLayoutAnimation = ResizeHeightAnimation(
            binding.constrainLayout, from, to, isHide, 300
        )
        binding.constrainLayout.startAnimation(resizeLayoutAnimation)
    }

    private fun startCollapseButtonRotateAnim(startPosition: Float) {
        buttonCollapseRotateAnimator?.cancel()
        buttonCollapseRotateAnimator = ObjectAnimator.ofFloat(
            binding.ivCollapse, View.ROTATION, startPosition, startPosition + 180f
        )
        buttonCollapseRotateAnimator?.duration = 300
        buttonCollapseRotateAnimator?.start()
    }
}