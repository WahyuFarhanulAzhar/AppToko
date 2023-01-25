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
import com.study.apptoko.adapter.NotaAdapter
import com.study.apptoko.api.BaseRetrofit
import com.study.apptoko.response.nota.NotaResponse
import com.study.apptoko.response.transaksi.Transaksi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class NotaFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val transaksi = arguments?.getParcelable<Transaksi>("transaksi")
        Log.d("data_transaksi", transaksi.toString())
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_nota, container, false)

        val txtNotaId = view.findViewById(R.id.txtNotaId) as TextView
        val txtNotaTanggal = view.findViewById(R.id.txtNotaTanggal) as TextView
        val txtNotaTotal = view.findViewById(R.id.txtNotaTotal) as TextView
        txtNotaId.text = "#"+transaksi!!.id.padStart(5, '0')
        txtNotaTanggal.text = transaksi.tanggal
        // Format ke rupiah
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        txtNotaTotal.text = numberFormat.format(transaksi!!.total.toDouble()).toString()

        getProduk(view, transaksi.id.toInt())

        return view
    }

    fun getProduk(view: View, transaksi_id: Int){
//    fun getProduk(view: View){
        val token = LoginActivity.sessionManager.getString("TOKEN")

        // Jalankan API getNota
        api.getNota(token.toString(), transaksi_id).enqueue(object : Callback<NotaResponse> {
            override fun onResponse(call: Call<NotaResponse>, response: Response<NotaResponse>) {
                Log.d("NotaData", response.body().toString())

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

                    val rv = view.findViewById(R.id.rv_nota) as RecyclerView

                    rv.setHasFixedSize(true)
                    rv.layoutManager = LinearLayoutManager(activity)
                    val rvAdapter = NotaAdapter(response.body()!!.data.item_transaksi)
                    rv.adapter = rvAdapter
                }
            }
            override fun onFailure(call: Call<NotaResponse>, t: Throwable) {
                Log.e("NotaError", t.toString())
            }
        })
    }

}