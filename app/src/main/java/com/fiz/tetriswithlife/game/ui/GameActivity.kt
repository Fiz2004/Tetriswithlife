package com.fiz.tetriswithlife.game.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.fiz.tetriswithlife.R
import com.fiz.tetriswithlife.databinding.ActivityGameBinding
import com.fiz.tetriswithlife.game.data.BitmapRepository
import com.fiz.tetriswithlife.game.domain.Display
import com.fiz.tetriswithlife.game.domain.GameState
import com.fiz.tetriswithlife.game.domain.character.TIMES_BREATH_LOSE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.max

@AndroidEntryPoint
class GameActivity : AppCompatActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    private var surfaceReady = mutableListOf(false, false)

    private lateinit var binding: ActivityGameBinding

    private lateinit var display: Display

    @Inject
    lateinit var bitmapRepository: BitmapRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gameViewModel.tryLoadState(savedInstanceState?.getSerializable(STATE) as? GameState)

        binding.gameSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {}

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                display = Display(
                    binding.gameSurfaceView.width,
                    binding.gameSurfaceView.height,
                    gameViewModel.gameState.grid.width,
                    gameViewModel.gameState.grid.height,
                    bitmapRepository
                )

                surfaceReady[0] = true
                if (surfaceReady.all { it })
                    gameViewModel.startGame(this@GameActivity)
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {}

        })

        binding.nextFigureSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {}

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                surfaceReady[1] = true
                if (surfaceReady.all { it })
                    gameViewModel.startGame(this@GameActivity)
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {}

        })

        bindListener()
    }

    fun displayUpdate() {

        binding.gameSurfaceView.holder.lockCanvas(null)?.let {
            display.render(gameViewModel.gameState, it)
            binding.gameSurfaceView.holder.unlockCanvasAndPost(it)
        }

        binding.nextFigureSurfaceView.holder.lockCanvas(null)?.let {
            display.renderInfo(gameViewModel.gameState, it)
            binding.nextFigureSurfaceView.holder.unlockCanvasAndPost(it)
        }

        setScoresTextView(gameViewModel.gameState.scores.toString().padStart(6, '0'))
        setRecordTextView(gameViewModel.gameState.record.toString().padStart(6, '0'))
        pauseButtonClick(gameViewModel.gameState.status)

        if (gameViewModel.gameState.status != "pause") {
            val sec: Double = if (gameViewModel.gameState.character.breath)
                TIMES_BREATH_LOSE
            else
                max(gameViewModel.gameState.character.timeBreath, 0.0)

            infoBreathTextviewChangeVisibility(gameViewModel.gameState.character.breath)
            val cl = ((floor(sec) * 255) / TIMES_BREATH_LOSE).toInt()
            breathTextviewChangeVisibilityAndColor(
                gameViewModel.gameState.character.breath,
                sec,
                cl
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bindListener() {
        this.binding.leftButton.setOnTouchListener { _: View, event: MotionEvent ->
            gameViewModel.clickLeftButton(event)
            true
        }

        binding.rightButton.setOnTouchListener { _: View, event: MotionEvent ->
            gameViewModel.clickRightButton(event)
            true
        }

        binding.downButton.setOnTouchListener { _: View, event: MotionEvent ->
            gameViewModel.clickDownButton(event)
            true
        }

        binding.rotateButton.setOnTouchListener { _: View, event: MotionEvent ->
            gameViewModel.clickRotateButton(event)
            true
        }

        binding.newGameButton.setOnClickListener {
            gameViewModel.clickNewGameButton()
        }
        binding.pauseButton.setOnClickListener {
            gameViewModel.clickPauseButton()
        }
        binding.exitButton.setOnClickListener {
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        surfaceReady = mutableListOf(false, false)
        gameViewModel.activityStop()
    }

    private fun setScoresTextView(scores: String) {
        binding.scoresTextView.post {
            binding.scoresTextView.text = resources.getString(
                R.string.scores_game_textview, scores.padStart
                    (6, '0')
            )
        }
    }

    private fun setRecordTextView(record: String) {
        binding.recordTextview.post {
            binding.recordTextview.text =
                resources.getString(R.string.record_game_textview, record.padStart(6, '0'))
        }
    }

    private fun pauseButtonClick(status: String) {
        binding.pauseButton.post {
            binding.pauseButton.text = if (status == "pause")
                resources.getString(R.string.resume_game_button)
            else
                resources.getString(R.string.pause_game_button)
        }
    }

    private fun infoBreathTextviewChangeVisibility(visibility: Boolean) {
        if (!visibility) {
            if (!binding.breathTextView.isVisible) {
                binding.infoBreathTextView.post {
                    binding.infoBreathTextView.visibility = View.VISIBLE
                }
            }
        } else if (binding.breathTextView.isVisible) {
            binding.infoBreathTextView.post {
                binding.infoBreathTextView.visibility = View.INVISIBLE
            }
        }
    }

    private fun breathTextviewChangeVisibilityAndColor(
        visibility: Boolean, sec: Double, color: Int
    ) {
        if (!visibility) {
            if (!binding.breathTextView.isVisible) {
                binding.breathTextView.post { binding.breathTextView.visibility = View.VISIBLE }
            }
            binding.breathTextView.post { binding.breathTextView.text = sec.toInt().toString() }
        } else if (binding.breathTextView.isVisible) {
            binding.breathTextView.post { binding.breathTextView.visibility = View.INVISIBLE }
        }

        binding.breathTextView.post {
            binding.breathTextView.setTextColor(
                Color.rgb(
                    255,
                    color,
                    color
                )
            )
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        gameViewModel.gameState.let {
//            outState.putSerializable("state", it)
        }
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val STATE = "state"
    }

}



