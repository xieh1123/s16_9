package com.example.s16_9

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    // 遊戲設定
    private val minNumber = 1
    private val maxNumber = 100
    private var targetNumber = 0
    private var attempts = 0
    private val maxAttempts = 10

    // UI元件 (使用您原本的變數名稱)
    private lateinit var edtext: EditText
    private lateinit var but1: Button
    private lateinit var textV1: TextView
    private lateinit var textv2: TextView
    private lateinit var text_time: TextView
    private lateinit var listv: ListView
    private val guessHistory = mutableListOf<String>()
    private lateinit var historyAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化UI元件 (使用您原本的變數名稱)
        edtext = findViewById(R.id.edtext)
        but1 = findViewById(R.id.but1)
        textV1 = findViewById(R.id.textV1)
        textv2 = findViewById(R.id.textv2)
        text_time = findViewById(R.id.text_time)
        listv = findViewById(R.id.listv)

        // 設定清單適配器
        historyAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, guessHistory)
        listv.adapter = historyAdapter

        // 開始新遊戲
        startNewGame()

        // 設定提交按鈕點擊事件
        but1.setOnClickListener {
            processGuess()
        }
    }

    private fun startNewGame() {
        // 產生隨機目標數字
        targetNumber = Random.nextInt(minNumber, maxNumber + 1)
        attempts = 0
        guessHistory.clear()
        historyAdapter.notifyDataSetChanged()

        // 更新UI
        updateAttemptsText()
        updateRangeText(minNumber, maxNumber)

        // 顯示遊戲開始提示
        showMessage("遊戲開始", "我已經想好了一個${minNumber}到${maxNumber}之間的數字，請猜猜看！")
    }

    private fun processGuess() {
        val guessText = edtext.text.toString()

        // 檢查輸入是否為有效數字
        if (guessText.isEmpty()) {
            showToast("請輸入一個數字")
            return
        }

        try {
            val guess = guessText.toInt()
            attempts++

            // 檢查數字是否在範圍內
            if (guess < minNumber || guess > maxNumber) {
                showToast("請輸入${minNumber}到${maxNumber}之間的數字")
                return
            }

            // 記錄猜測歷史
            val result = when {
                guess < targetNumber -> "$guess: 太小了"
                guess > targetNumber -> "$guess: 太大了"
                else -> "$guess: 恭喜猜對了！"
            }
            guessHistory.add(result)
            historyAdapter.notifyDataSetChanged()

            // 處理遊戲結果
            when {
                guess == targetNumber -> {
                    // 猜對了，顯示勝利訊息
                    showGameOverDialog(true, "恭喜你猜對了！目標數字就是 $targetNumber，你總共猜了 $attempts 次。")
                }
                attempts >= maxAttempts -> {
                    // 已達最大嘗試次數，顯示失敗訊息
                    showGameOverDialog(false, "很遺憾，你已用完 $maxAttempts 次機會。目標數字就是 $targetNumber。")
                }
                guess < targetNumber -> {
                    // 猜的數字太小
                    updateRangeText(guess + 1, maxNumber)
                    showToast("太小了")
                }
                else -> {
                    // 猜的數字太大
                    updateRangeText(minNumber, guess - 1)
                    showToast("太大了")
                }
            }

            // 更新嘗試次數顯示
            updateAttemptsText()

            // 清空輸入框
            edtext.text.clear()

        } catch (e: NumberFormatException) {
            showToast("請輸入有效的數字")
        }
    }

    private fun updateAttemptsText() {
        text_time.text = "已試 $attempts 次 / 共 $maxAttempts 次"
    }

    private fun updateRangeText(min: Int, max: Int) {
        textv2.text = "有效範圍: $min - $max"
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showMessage(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("確定", null)
            .show()
    }

    private fun showGameOverDialog(isWin: Boolean, message: String) {
        AlertDialog.Builder(this)
            .setTitle(if (isWin) "恭喜獲勝" else "遊戲結束")
            .setMessage(message)
            .setPositiveButton("再玩一次") { _, _ ->
                startNewGame()
            }
            .setCancelable(false)
            .show()
    }
}