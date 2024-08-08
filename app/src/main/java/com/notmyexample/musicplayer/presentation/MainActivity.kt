package com.notmyexample.musicplayer.presentation

import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.bumptech.glide.Glide
import com.notmyexample.musicplayer.R
import com.notmyexample.musicplayer.data.model.Song
import com.notmyexample.musicplayer.databinding.ActivityMainBinding
import com.notmyexample.musicplayer.presentation.navigator.AppNavigator
import com.notmyexample.musicplayer.presentation.navigator.Screens
import com.notmyexample.musicplayer.presentation.navigator.Screens.ALBUM
import com.notmyexample.musicplayer.presentation.navigator.Screens.DETAIL_ALBUM
import com.notmyexample.musicplayer.presentation.navigator.Screens.FAVOURITE
import com.notmyexample.musicplayer.presentation.navigator.Screens.HOME
import com.notmyexample.musicplayer.presentation.navigator.Screens.PLAY
import com.notmyexample.musicplayer.presentation.navigator.Screens.SEARCH
import com.notmyexample.musicplayer.presentation.navigator.Screens.SONGS
import com.notmyexample.musicplayer.presentation.service.PlaySongManager
import com.notmyexample.musicplayer.presentation.service.PlaySongService
import com.notmyexample.musicplayer.utils.constant.PlayEventEConstant.EVENT_NAME
import com.notmyexample.musicplayer.utils.constant.PlayEventEConstant.EVENT_PAUSE
import com.notmyexample.musicplayer.utils.constant.PlayEventEConstant.EVENT_PLAY
import com.notmyexample.musicplayer.utils.formatTime
import com.notmyexample.musicplayer.utils.requestPermission
import com.notmyexample.musicplayer.utils.requestPermissions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var playSongManager: PlaySongManager

    @Inject
    lateinit var appNavigator: AppNavigator

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        requestPermissions()
        initView()
        handleEvent()
        observeData()
    }

    private fun initView() {
        appNavigator.navigateTo(HOME)
    }

    private fun handleEvent() {
        setOnBackPressed()

        binding.lnlHomeNav.setOnClickListener { appNavigator.navigateTo(HOME) }
        binding.lnlSearchNav.setOnClickListener { appNavigator.navigateTo(SEARCH) }
        binding.lnlFavouriteNav.setOnClickListener { appNavigator.navigateTo(FAVOURITE) }

        binding.cslBottomPlayBar.setOnClickListener {
            appNavigator.navigateTo(
                PLAY,
                R.anim.enter_from_bottom,
                R.anim.anim_nothing,
                R.anim.anim_nothing,
                R.anim.exit_to_bottom
            )
        }

        binding.ivPlayOrPause.setOnClickListener {
            val isPlaying = playSongManager.isPlayingLiveData.value
            if (isPlaying == true) {
                playSongManager.pauseCurrentSong()
                startPlaySongService(EVENT_PAUSE)
            } else {
                playSongManager.resumeCurrentSong()
                startPlaySongService(EVENT_PLAY)
            }
        }
    }

    private fun observeData() {
        with(playSongManager) {
            currentPlayLiveData.observe(this@MainActivity) {
                appNavigator.getCurrentScreen()?.let { screen ->
                    updateBottomPlayBar(it, screen)
                }
                binding.tvDuration.text = it?.duration?.let { duration -> formatTime(duration) }
            }

            isPlayingLiveData.observe(this@MainActivity) {
                if (it) {
                    binding.ivPlayOrPause.setImageResource(R.drawable.ic_pause_white)
                } else {
                    binding.ivPlayOrPause.setImageResource(R.drawable.ic_play_white)
                }
            }

            currentPositionLiveData.observe(this@MainActivity) {
                binding.tvPosition.text = formatTime(it)
            }
        }
    }

    fun updateBottomBar(screen: Screens) {
        clearBottomNavigationButtonState()
        updateBottomPlayBar(playSongManager.currentPlayLiveData.value, screen)
        when (screen) {
            HOME, ALBUM, DETAIL_ALBUM -> {
                binding.ivHome.setImageDrawable(getDrawable(this, R.drawable.ic_home_selected))
                binding.tvHome.setTextColor(getColor(R.color.black))
            }

            FAVOURITE -> {
                binding.ivFavourite.setImageDrawable(
                    getDrawable(
                        this,
                        R.drawable.ic_favourite_fill_selected
                    )
                )
                binding.tvFavourite.setTextColor(getColor(R.color.black))
            }

            SEARCH -> {
                binding.ivSearch.setImageDrawable(getDrawable(this, R.drawable.ic_search_selected))
                binding.tvSearch.setTextColor(getColor(R.color.black))
            }

            PLAY, SONGS -> {
                binding.lnlBottomNavigationBar.visibility = View.GONE
            }
        }
    }

    private fun clearBottomNavigationButtonState() {
        binding.lnlBottomNavigationBar.visibility = View.VISIBLE

        binding.ivHome.setImageDrawable(getDrawable(this, R.drawable.ic_home))
        binding.tvHome.setTextColor(getColor(R.color.gray600))

        binding.ivFavourite.setImageDrawable(getDrawable(this, R.drawable.ic_favourite_fill))
        binding.tvFavourite.setTextColor(getColor(R.color.gray600))

        binding.ivSearch.setImageDrawable(getDrawable(this, R.drawable.ic_search))
        binding.tvSearch.setTextColor(getColor(R.color.gray600))
    }

    private fun updateBottomPlayBar(song: Song?, screen: Screens) {
        if (song != null && screen != PLAY) {
            binding.cslBottomPlayBar.visibility = View.VISIBLE
            binding.tvSongName.text = song.name
            binding.tvAuthor.text = song.author
            Glide.with(this).load(song.thumbnail).into(binding.ivThumbnail)
        } else {
            binding.cslBottomPlayBar.visibility = View.GONE
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(POST_NOTIFICATIONS, READ_MEDIA_AUDIO))
        } else {
            requestPermission(READ_EXTERNAL_STORAGE)
        }
    }

    fun startPlaySongService(event: Int) {
        val intent = Intent(this, PlaySongService::class.java)
        intent.putExtra(EVENT_NAME, event)
        startService(intent)
    }

    private fun setOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                appNavigator.popBackStack {
                    val alertDialog = AlertDialog.Builder(this@MainActivity).apply {
                        setTitle(R.string.do_you_want_to_exit)
                        setPositiveButton(R.string.yes) { _, _ -> finishAffinity() }
                        setNegativeButton(R.string.no) { _, _ -> }
                    }
                    alertDialog.show()
                }
            }
        })
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}