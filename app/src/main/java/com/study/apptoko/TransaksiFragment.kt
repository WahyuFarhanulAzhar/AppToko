package com.study.apptoko

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.study.apptoko.adapter.TransaksiAdapter
import com.study.apptoko.api.BaseRetrofit
import com.study.apptoko.response.cart.Cart
import com.study.apptoko.response.produk.ProdukResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class TransaksiFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint }
    private lateinit var my_cart : ArrayList<Cart>
    private lateinit var total_bayar : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transaksi, container, false)

        getProduk(view)

        val btnBayar =  view.findViewById<Button>(R.id.btnBayar)
        btnBayar.setOnClickListener {
            // Memastikan my_cart sudah di inisialisasi (lateinit)
            if (this::my_cart.isInitialized) {
                // Memastikan ada produk yang dibeli sebelum dibayar
                if(my_cart.isEmpty()){
                    Toast.makeText(activity?.applicationContext, "Tidak ada produk yang dibeli", Toast.LENGTH_LONG).show()
                }else{
                    val bundle = Bundle()
                    bundle.putParcelableArrayList("MY_CART", my_cart)
                    bundle.putString("TOTAL", total_bayar)

                    findNavController().navigate(R.id.bayarFragment, bundle)
                }
            }else{
                Toast.makeText(activity?.applicationContext, "Tidak ada produk yang dibeli", Toast.LENGTH_LONG).show()
            }
        }

        return view
    }

// * seharusnya digabungkan menjadi satu dengan produk
    fun getProduk(view: View){
        val token = LoginActivity.sessionManager.getString("TOKEN")

        // Jalankan API getProduk
        api.getProduk(token.toString()).enqueue(object : Callback<ProdukResponse> {
            override fun onResponse(
                call: Call<ProdukResponse>,
                response: Response<ProdukResponse>
            ) {
                // Log d -> log debug
                Log.d("ProdukData", response.body().toString())

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
                    // Tampilkan data produk menggunakan recycler view
                    val rv = view.findViewById(R.id.rv_transaksi) as RecyclerView

                    rv.setHasFixedSize(true)
                    rv.layoutManager = LinearLayoutManager(activity)
                    val rvAdapter = TransaksiAdapter(response.body()!!.data.produk)
                    rv.adapter = rvAdapter

                    rvAdapter.callbackInterface = object : CallbackInterface {
                        override fun passResultCallback(total: String, cart: ArrayList<Cart>) {
                            val txtTotalBayar = activity?.findViewById<TextView>(R.id.txtTotalBayar)

                            // Format ke rupiah
                            val localeID = Locale("in", "ID")
                            val numberFormat = NumberFormat.getCurrencyInstance(localeID)

                            total_bayar = total
                            my_cart = cart

                            txtTotalBayar?.setText(numberFormat.format(total.toDouble()).toString())

                            Log.d("MyCart", cart.toString())
                        }

                    }
                }
            }

            override fun onFailure(call: Call<ProdukResponse>, t: Throwable) {
                // Log d -> log error
                Log.e("TransaksiError", t.toString())
            }

        })
    }

}