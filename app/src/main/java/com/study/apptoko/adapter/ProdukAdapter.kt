package com.study.apptoko.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.study.apptoko.LoginActivity
import com.study.apptoko.R
import com.study.apptoko.api.BaseRetrofit
import com.study.apptoko.response.produk.Produk
import com.study.apptoko.response.produk.ProdukResponsePost
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class ProdukAdapter(val listProduk: List<Produk>):RecyclerView.Adapter<ProdukAdapter.ViewHolder>() {

    private val api by lazy { BaseRetrofit().endpoint }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produk, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = listProduk[position]
        holder.txtNamaProduk.text = produk.nama
        // Format ke rupiah
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)

        holder.txtHarga.text = numberFormat.format(produk.harga.toDouble()).toString()
        if (produk.stok.toInt() > 0){
            holder.txtStok.text = "Tersedia " + produk.stok
        } else{
            holder.txtStok.text = "Stok habis"
            holder.txtStok.setTextColor(Color.RED)
        }

        holder.btnDelete.setOnClickListener {
            Toast.makeText(holder.itemView.context,produk.nama, Toast.LENGTH_LONG).show()

            val token = LoginActivity.sessionManager.getString("TOKEN")

            api.deleteProduk(token.toString(), produk.id.toInt()).enqueue(
                object :
                    Callback<ProdukResponsePost> {
                    override fun onResponse(
                        call: Call<ProdukResponsePost>,
                        response: Response<ProdukResponsePost>
                    ) {
                        Log.d("DeleteData", response.toString())

                        // Handle opabila token expired
                        val success = response.body()!!.success
                        if(success==false){     // Cek apakah gagal
                            val message = response.body()!!.message
                            if (message == "Token tidak valid"){    // Cek apakah eror karena token expired
                                Toast.makeText(holder.itemView.context, "Token expired, silahkan login kembali "+produk.nama, Toast.LENGTH_LONG).show()
                                // Hapus data session
                                LoginActivity.sessionManager.clearSession()
                                // Pindah ke activity login
                                val context=holder.txtNamaProduk.context
                                val moveIntent = Intent( context, LoginActivity::class.java)
                                context.startActivity(moveIntent)
                                (context as Activity).finish()
                            }
                        }else {
                            Toast.makeText(
                                holder.itemView.context,
                                "Data di hapus",
                                Toast.LENGTH_LONG
                            ).show()

                            holder.itemView.findNavController().navigate(R.id.produkFragment)
                        }
                    }

                    override fun onFailure(call: Call<ProdukResponsePost>, t: Throwable) {
                        Log.e("ProdukError", t.toString())
                    }

                }
            )
        }
        holder.btnEdit.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Edit "+produk.nama, Toast.LENGTH_LONG).show()

            val bundle = Bundle()
            bundle.putParcelable("produk",produk)
            bundle.putString("status","edit")

            holder.itemView.findNavController().navigate(R.id.produkFormFragment,bundle)
        }

    }

    override fun getItemCount(): Int {
        return listProduk.size
    }

    class ViewHolder(ItemView : View) : RecyclerView.ViewHolder(ItemView){
        val txtNamaProduk = itemView.findViewById(R.id.txtNamaProduk) as TextView
        val txtHarga = itemView.findViewById(R.id.txtHarga) as TextView
        val txtStok = itemView.findViewById(R.id.txtStok) as TextView
        val btnDelete = itemView.findViewById(R.id.btnDelete) as ImageButton
        val btnEdit = itemView.findViewById(R.id.btnEdit) as ImageButton
    }
}