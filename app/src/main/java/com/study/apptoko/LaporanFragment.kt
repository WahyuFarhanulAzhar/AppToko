package com.study.apptoko

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.study.apptoko.adapter.LaporanAdapter
import com.study.apptoko.api.BaseRetrofit
import com.study.apptoko.response.transaksi.TransaksiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class LaporanFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_laporan, container, false)

        getLaporan(view)

        return view
    }

    fun getLaporan(view: View){
        val token = LoginActivity.sessionManager.getString("TOKEN")

        // Jalankan API getProduk
        api.getTransaksi(token.toString()).enqueue(object : Callback<TransaksiResponse> {
            override fun onResponse(
                call: Call<TransaksiResponse>,
                response: Response<TransaksiResponse>
            ) {
                Log.d("LaporanData", response.body().toString())

                // Handle opabila token expired
                val success = response.body()!!.success
                if(success==false){     // Cek apakah gagal
                    val message = response.body()!!.message
                    if (message == "Token tidak valid"){    // Cek apakah eror karena token expired
                        Toast.makeText(activity?.applicationContext, "Token expired, silahkan login kembali", Toast.LENGTH_LONG).show()
                        // Hapus data session
                        LoginActivity.sessionManager.clearSession()
                        // Pindah ke activity login
                        val moveIntent = Intent(activity, LoginActivity::class.java)
                        startActivity(moveIntent)
                        activity?.finish()
                    }
                }else {
                    val rv = view.findViewById(R.id.rv_laporan) as RecyclerView

                    val txtTotalPendapatan = view.findViewById(R.id.txtTotalPendapatan) as TextView
                    val totalPendapatan = response.body()!!.data.total

                    // Format ke rupiah
                    val localeID = Locale("in", "ID")
                    val numberFormat = NumberFormat.getCurrencyInstance(localeID)
                    // Tampilkan Total Pendapatan
                    txtTotalPendapatan.text =
                        numberFormat.format(totalPendapatan.toDouble()).toString()

                    rv.setHasFixedSize(true)
                    rv.layoutManager = LinearLayoutManager(activity)
                    val rvAdapter = LaporanAdapter(response.body()!!.data.transaksi)
                    rv.adapter = rvAdapter
                }
            }

            override fun onFailure(call: Call<TransaksiResponse>, t: Throwable) {
                // Log d -> log error
                Log.e("LaporanResponseError", t.toString())
            }
        })
    }

}