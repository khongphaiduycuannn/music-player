package com.notmyexample.musicplayer.presentation

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.notmyexample.musicplayer.R
import com.notmyexample.musicplayer.databinding.ActivityMainBinding
import com.notmyexample.musicplayer.presentation.navigator.AppNavigator
import com.notmyexample.musicplayer.presentation.navigator.Screens
import com.notmyexample.musicplayer.presentation.navigator.Screens.FAVOURITE
import com.notmyexample.musicplayer.presentation.navigator.Screens.HOME
import com.notmyexample.musicplayer.presentation.navigator.Screens.PLAY
import com.notmyexample.musicplayer.presentation.navigator.Screens.SEARCH
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var appNavigator: AppNavigator

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        appNavigator.navigateTo(HOME)

        handleEvent()
    }

    private fun handleEvent() {
        setOnBackPressed()

        binding.lnlHomeNav.setOnClickListener { appNavigator.navigateTo(HOME) }
        binding.lnlSearchNav.setOnClickListener { appNavigator.navigateTo(SEARCH) }
        binding.lnlFavouriteNav.setOnClickListener { appNavigator.navigateTo(FAVOURITE) }
    }

    fun updateBottomNavigationBar(screen: Screens) {
        clearBottomNavigationButtonState()
        when (screen) {
            HOME -> {
                binding.ivHome.setImageDrawable(getDrawable(this, R.drawable.ic_home_selected))
                binding.tvHome.setTextColor(getColor(R.color.black))
            }

            FAVOURITE -> {
                binding.ivFavourite.setImageDrawable(
                    getDrawable(
                        this,
                        R.drawable.ic_favourite_selected
                    )
                )
                binding.tvFavourite.setTextColor(getColor(R.color.black))
            }

            SEARCH -> {
                binding.ivSearch.setImageDrawable(getDrawable(this, R.drawable.ic_search_selected))
                binding.tvSearch.setTextColor(getColor(R.color.black))
            }

            PLAY -> {
                binding.lnlBottomNavigationBar.visibility = View.GONE
            }
        }
    }

    private fun clearBottomNavigationButtonState() {
        binding.lnlBottomNavigationBar.visibility = View.VISIBLE

        binding.ivHome.setImageDrawable(getDrawable(this, R.drawable.ic_home))
        binding.tvHome.setTextColor(getColor(R.color.gray_600))

        binding.ivFavourite.setImageDrawable(getDrawable(this, R.drawable.ic_favourite))
        binding.tvFavourite.setTextColor(getColor(R.color.gray_600))

        binding.ivSearch.setImageDrawable(getDrawable(this, R.drawable.ic_search))
        binding.tvSearch.setTextColor(getColor(R.color.gray_600))
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