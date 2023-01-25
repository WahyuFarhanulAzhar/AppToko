package com.study.apptoko.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.study.apptoko.CallbackInterface
import com.study.apptoko.R
import com.study.apptoko.response.cart.Cart
import com.study.apptoko.response.produk.Produk
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class TransaksiAdapter (val listProduk: List<Produk>): RecyclerView.Adapter<TransaksiAdapter.ViewHolder>() {

    var callbackInterface: CallbackInterface? = null
    var total: Int = 0
    var cart: ArrayList<Cart> = arrayListOf<Cart>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaksiAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaksi, parent, false)
        return TransaksiAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransaksiAdapter.ViewHolder, position: Int) {
        val produk = listProduk[position]
        holder.txtNamaProduk.text = produk.nama
        // Tampilkan jumlah stok yg tersedia
        if (produk.stok.toInt() > 0){
            holder.txtTransaksiStok.text = "Tersedia " + produk.stok
        } else{
            holder.txtTransaksiStok.text = "Stok habis"
            holder.txtTransaksiStok.setTextColor(Color.RED)
        }
        // Format ke rupiah
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)

        holder.txtHarga.text = numberFormat.format(produk.harga.toDouble()).toString()

        holder.btnPlus.setOnClickListener {
            val old_value = holder.txtQty.text.toString().toInt()
            val new_value = old_value+1

            // Tidak bisa tambah lebih dari stok yg ada
            if(new_value > produk.stok.toInt()){
                Toast.makeText(holder.itemView.context, "Stok kurang", Toast.LENGTH_LONG).show()
            }else{
                holder.txtQty.setText(new_value.toString())
                val subtotal = produk.harga.toInt() * new_value
                holder.txtTransaksiSubtotal.setText(numberFormat.format(subtotal.toDouble()).toString())

                // Rubah nilai Total
                total = total + produk.harga.toString().toInt()

                // Cek apabila sudah ada data di cart, kemudian hapus
                val index = cart.indexOfFirst { it.id == produk.id.toInt() }.toInt()
                if(index!=-1){
                    cart.removeAt(index)
                }
                // Tambahkan data cart baru
                val itemCart = Cart(produk.id.toInt(),produk.harga.toInt(),new_value)
                cart.add(itemCart)

                // Kirim data total bayar dan cart baru
                callbackInterface?.passResultCallback(total.toString(),cart)
            }

        }

        holder.btnMinus.setOnClickListener {
            val old_value = holder.txtQty.text.toString().toInt()
            val new_value = old_value-1

            // Cek apabila sudah ada data di cart, kemudian hapus
            val index = cart.indexOfFirst { it.id == produk.id.toInt() }.toInt()

            if(index!=-1){
                cart.removeAt(index)
            }

            // Biarkan data terhapus apabila jumlah nol
            if (new_value>=0){
                holder.txtQty.setText(new_value.toString())
                val subtotal = produk.harga.toInt() * new_value
                holder.txtTransaksiSubtotal.setText(numberFormat.format(subtotal.toDouble()).toString())

                total = total - produk.harga.toString().toInt()
            }
            // Hanya kurangi kalau nilai lebih dari satu
            if (new_value>=1){
                // Tambahkan data cart baru
                val itemCart = Cart(produk.id.toInt(),produk.harga.toInt(),new_value)
                cart.add(itemCart)
            }

            // Kirim data total bayar baru
            callbackInterface?.passResultCallback(total.toString(),cart)
        }

    }

    override fun getItemCount(): Int {
        return listProduk.size
    }

    class ViewHolder(ItemView : View) : RecyclerView.ViewHolder(ItemView) {
        val txtNamaProduk = itemView.findViewById(R.id.txtNamaProduk) as TextView
        val txtHarga = itemView.findViewById(R.id.txtHarga) as TextView
        val txtTransaksiStok = itemView.findViewById(R.id.txtTransaksiStok) as TextView
        val txtTransaksiSubtotal = itemView.findViewById(R.id.txtTransaksiSubtotal) as TextView
        val txtQty = itemView.findViewById(R.id.txtQty) as TextView
        val btnPlus = itemView.findViewById(R.id.btnPlus) as ImageButton
        val btnMinus = itemView.findViewById(R.id.btnMinus) as ImageButton
    }
}