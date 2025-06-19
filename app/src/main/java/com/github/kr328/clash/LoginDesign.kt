package com.github.kr328.clash.design

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.github.kr328.clash.R

class LoginDesign(context: Context) : Design<LoginDesign.Request>(context) {
    val root: View = View.inflate(context, R.layout.design_login, null)
    val usernameEdit: EditText = root.findViewById(R.id.usernameEdit)
    val passwordEdit: EditText = root.findViewById(R.id.passwordEdit)
    val loginButton: Button = root.findViewById(R.id.loginButton)
    val supportButton: Button = root.findViewById(R.id.supportButton)
    val announcementButton: Button = root.findViewById(R.id.announcementButton)
    fun showAnnouncementPopup(content: String) {
        AlertDialog.Builder(root.context)
            .setTitle("公告")
            .setMessage(content)
            .setPositiveButton("知道了", null)
            .show()
    }
    enum class Request {
        Login, Register, ForgotPassword
    }
}
