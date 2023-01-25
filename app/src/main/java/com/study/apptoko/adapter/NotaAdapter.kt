package com.study.apptoko.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.study.apptoko.R
import com.study.apptoko.response.nota.ItemTransaksi
import java.text.NumberFormat
import java.util.*

class NotaAdapter(val listNota: List<ItemTransaksi>): RecyclerView.Adapter<NotaAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_nota, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotaAdapter.ViewHolder, position: Int) {
        val produk = listNota[position]
        // Format ke rupiah
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)

        holder.txtNotaNama.text = produk.nama
        holder.txtNotaHarga.text = numberFormat.format(produk.harga_saat_transaksi.toDouble()).toString()
        holder.txtNotaTerjual.text = "Terjual "+produk.qty
        holder.txtNotaSubtotal.text = numberFormat.format(produk.sub_total.toDouble()).toString()
    }

    override fun getItemCount(): Int {
        return listNota.size
    }

    class ViewHolder(ItemView : View) : RecyclerView.ViewHolder(ItemView) {
        val txtNotaNama = itemView.findViewById(R.id.txtNotaNama) as TextView
        val txtNotaHarga = itemView.findViewById(R.id.txtNotaHarga) as TextView
        val txtNotaTerjual = itemView.findViewById(R.id.txtNotaTerjual) as TextView
        val txtNotaSubtotal = itemView.findViewById(R.id.txtNotaSubtotal) as TextView
    }

}