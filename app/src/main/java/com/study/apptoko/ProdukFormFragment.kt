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
import com.google.android.material.textfield.TextInputEditText
import com.study.apptoko.api.BaseRetrofit
import com.study.apptoko.response.produk.Produk
import com.study.apptoko.response.produk.ProdukResponsePost
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProdukFormFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_produk_form, container, false)
        val txtHalamanProduk = view.findViewById<TextView>(R.id.txtHalamanProduk)

        val btnProsesProduk = view.findViewById<Button>(R.id.btnProsesProduk)

        val txtFormNama = view.findViewById<TextView>(R.id.txtFormNama)
        val txtFormHarga = view.findViewById<TextView>(R.id.txtFormHarga)
        val txtFormStok = view.findViewById<TextView>(R.id.txtFormStok)

        val status = arguments?.getString("status")
        val produk = arguments?.getParcelable<Produk>("produk")

        Log.d("produkForm",produk.toString())

        if (status=="edit"){
            txtHalamanProduk.setText("Edit " + produk?.nama)
            txtFormNama.setText(produk?.nama.toString())
            txtFormHarga.setText(produk?.harga.toString())
            txtFormStok.setText(produk?.stok.toString())
        }

        btnProsesProduk.setOnClickListener{
            val txtFormNama = view.findViewById<TextInputEditText>(R.id.txtFormNama)
            val txtFormHarga = view.findViewById<TextInputEditText>(R.id.txtFormHarga)
            val txtFormStok = view.findViewById<TextInputEditText>(R.id.txtFormStok)

            val token = LoginActivity.sessionManager.getString("TOKEN")
            val adminId = LoginActivity.sessionManager.getString("ADMIN_ID")

            if (status=="edit"){
                // Edit Produk
                api.putProduk(token.toString(), produk?.id.toString().toInt(), adminId.toString().toInt(), txtFormNama.text.toString(), txtFormHarga.text.toString().toInt(), txtFormStok.text.toString().toInt()).enqueue(
                    object :
                        Callback<ProdukResponsePost> {
                        override fun onResponse(
                            call: Call<ProdukResponsePost>,
                            response: Response<ProdukResponsePost>
                        ) {
                            Log.d("ResponData", response.body()!!.data.toString())

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
                                Toast.makeText(activity?.applicationContext,"Data " + response.body()!!.data.produk.nama.toString() + " di edit", Toast.LENGTH_LONG).show()

                                findNavController().navigate(R.id.produkFragment)
                            }
                        }
                        override fun onFailure(call: Call<ProdukResponsePost>, t: Throwable) {
                            Log.e("Error", t.toString())
                        }
                    }
                )
            } else{
                // Tambah Produk
                api.postProduk(token.toString(), adminId.toString().toInt(), txtFormNama.text.toString(), txtFormHarga.text.toString().toInt(), txtFormStok.text.toString().toInt()).enqueue(
                    object :
                        Callback<ProdukResponsePost> {
                        override fun onResponse(
                            call: Call<ProdukResponsePost>,
                            response: Response<ProdukResponsePost>
                        ) {
                            Log.d("Data", response.toString())

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
                                Toast.makeText(activity?.applicationContext,"Data di input",Toast.LENGTH_LONG).show()

                                findNavController().navigate(R.id.produkFragment)
                            }
                        }
                        override fun onFailure(call: Call<ProdukResponsePost>, t: Throwable) {
                            Log.e("Error", t.toString())
                        }
                    }
                )
            }

        }

        return view
    }

}