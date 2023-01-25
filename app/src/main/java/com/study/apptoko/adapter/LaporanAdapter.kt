package com.study.apptoko.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.study.apptoko.R
import com.study.apptoko.response.transaksi.Transaksi
import java.text.NumberFormat
import java.util.*

class LaporanAdapter(val listTransaksi: List<Transaksi>):RecyclerView.Adapter<LaporanAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_laporan, parent, false)
        return LaporanAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaksi = listTransaksi[position]
        holder.txtTglTransaksi.text = transaksi.tanggal
        holder.txtNoNota.text = "#"+transaksi.id.padStart(5, '0')

        // Format ke rupiah
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        holder.txtItemTotalTrans.text = numberFormat.format(transaksi.total.toDouble()).toString()

        holder.btnDetailNota.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Detail Nota #"+transaksi.id.padStart(5, '0'), Toast.LENGTH_LONG).show()

            val bundle = Bundle()
            bundle.putParcelable("transaksi",transaksi)

            holder.itemView.findNavController().navigate(R.id.notaFragment,bundle)
        }
    }

    override fun getItemCount(): Int {
        return listTransaksi.size
    }

    class ViewHolder(ItemView : View) : RecyclerView.ViewHolder(ItemView){
        val txtTglTransaksi = itemView.findViewById(R.id.txtTglTransaksi) as TextView
        val txtNoNota = itemView.findViewById(R.id.txtNoNota) as TextView
        val txtItemTotalTrans = itemView.findViewById(R.id.txtItemTotalTrans) as TextView
        val btnDetailNota = itemView.findViewById(R.id.btnDetailNota) as Button

    }

}