package com.github.kr328.clash

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.github.kr328.clash.design.LoginDesign
import com.github.kr328.clash.service.V2boardService
import kotlinx.coroutines.*
import com.github.kr328.clash.service.model.Profile

class LoginActivity : BaseActivity<LoginDesign>() {
    private lateinit var v2boardService: V2boardService

    override suspend fun main() {
        v2boardService = V2boardService(this)
        val design = LoginDesign(this)
        setContentDesign(design)

        // 公告弹窗（首次登录只弹一次）
        val sp = getSharedPreferences("v2board", MODE_PRIVATE)
        if (!sp.getBoolean("show_announcement", false)) {
            val announcement = withContext(Dispatchers.IO) { v2boardService.getAnnouncement() }
            if (!announcement.isNullOrEmpty()) {
                design.showAnnouncementPopup(announcement)
            }
            sp.edit().putBoolean("show_announcement", true).apply()
        }
        // 公告按钮
        design.announcementButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val announcement = withContext(Dispatchers.IO) { v2boardService.getAnnouncement() }
                if (!announcement.isNullOrEmpty()) {
                    design.showAnnouncementPopup(announcement)
                } else {
                    Toast.makeText(this@LoginActivity, "暂无公告", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // 客服按钮
        design.supportButton.setOnClickListener {
            startActivity(Intent(this, SupportWebActivity::class.java))
        }
        // 登录按钮
        design.loginButton.setOnClickListener {
            val username = design.usernameEdit.text.toString()
            val password = design.passwordEdit.text.toString()
            GlobalScope.launch(Dispatchers.IO) {
                val token = v2boardService.login(username, password)
                if (token != null) {
                    val subUrl = v2boardService.getSubscribeUrl(token)
                    if (subUrl != null) {
                        withProfile { create(Profile.Type.Url, "v2board订阅", subUrl) }
                    }
                    withContext(Dispatchers.Main) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "登录失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
