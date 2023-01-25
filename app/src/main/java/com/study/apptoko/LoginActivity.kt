package com.study.apptoko

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.study.apptoko.api.BaseRetrofit
import com.study.apptoko.response.login.LoginResponse
import com.study.apptoko.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    companion object{
        // lateinit = lazy, tetapi memakai var sehingga isi variabel dapat dirubah
        lateinit var sessionManager: SessionManager
        private lateinit var context: Context
    }

    // inisialisasi API
    // sama dengan private val api = BaseRetrofit().endpoint, tetapi by lazy lebih optimal untuk memory karena hanya akan di inisialisasi saat digunakan
    private val api by lazy { BaseRetrofit().endpoint }

    // Jalankan saat inisialisasi activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        // Cek status login, apbila sudah login, langung pindah ke MainActivity
        val loginStatus = sessionManager.getBoolean("LOGIN_STATUS")
        if(loginStatus){
            val moveIntent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(moveIntent)
            finish()
        }

        // Deklarasi komponen sesuai UI di Layout
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtEmail = findViewById<TextInputEditText>(R.id.txtEmail)
        val txtPassword = findViewById<TextInputEditText>(R.id.txtPassword)

        // Buat listener dan tindakan saat button di klik
        btnLogin.setOnClickListener {
            // Jalankan API login
            api.login(txtEmail.text.toString(), txtPassword.text.toString()).enqueue(
                object :
                Callback<LoginResponse> {
                    // Apabila mendapat respon
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        Log.e("LoginData",response.toString())
                        // Cek dari respon data success
                        val correct = response.body()!!.success

                        // Apabila success -> true
                        if(correct){
                            val token = response.body()!!.data.token
                            // Simpan data token ke Shared Preferences
                            sessionManager.saveString("TOKEN", "Bearer "+token)
                            sessionManager.saveBoolean("LOGIN_STATUS", true)
                            sessionManager.saveString("ADMIN_ID", response.body()!!.data.admin.id.toString())

                            // Pindah ke MainActivity
                            val moveIntent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(moveIntent)
                            finish()
                        }else{ // Apabila success -> false
                            Toast.makeText(applicationContext, "Email atau password salah", Toast.LENGTH_LONG).show()
                        }
                    }
                    // Apabila error
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.e("LoginError",t.toString())
                    }
                }
            )
        }
    }
}