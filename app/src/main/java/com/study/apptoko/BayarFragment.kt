package com.study.apptoko

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.study.apptoko.api.BaseRetrofit
import com.study.apptoko.response.cart.Cart
import com.study.apptoko.response.itemTransaksi.ItemTransaksiResponsePost
import com.study.apptoko.response.transaksi.TransaksiResponsePost
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*


class BayarFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bayar, container, false)

        val total = arguments?.getString("TOTAL")
        val my_cart = arguments?.getParcelableArrayList<Cart>("MY_CART")

        // Format ke rupiah
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)

        val txtKembalian = view.findViewById<TextView>(R.id.txtKembalian)

        val txtTotalTransaksiBayar = view.findViewById<TextView>(R.id.txtTotalTransaksiBayar)
        txtTotalTransaksiBayar.setText((numberFormat.format(total?.toDouble()).toString()))

        val txtPenerimaan = view.findViewById<EditText>(R.id.txtPenerimaan)

        txtPenerimaan.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            // Apabila input penerimaan berubah, lakukan dibawah setelah berubah
            override fun afterTextChanged(s: Editable?) {
                var kembalian : Int = 0

                if (txtPenerimaan.text.toString() != ""){
                    val penerimaan: Int = txtPenerimaan.text.toString().toInt()
                    kembalian = penerimaan - total.toString().toInt()

                    if (kembalian>0){
                        txtKembalian.setText(numberFormat.format(kembalian.toDouble()).toString())
                    }else{
                        txtKembalian.setText(numberFormat.format(0).toString())
                    }
                }
            }
        })

        val token = LoginActivity.sessionManager.getString("TOKEN")
        val adminId = LoginActivity.sessionManager.getString("ADMIN_ID")

        val btnSimpanBayar = view.findViewById<Button>(R.id.btnSimpanBayar)
        btnSimpanBayar.setOnClickListener {
            // Tambah Transaksi
            api.postTransaksi(token.toString(), adminId.toString().toInt(),total.toString().toInt()).enqueue(
                object :
                    Callback<TransaksiResponsePost> {
                    override fun onResponse(
                        call: Call<TransaksiResponsePost>,
                        response: Response<TransaksiResponsePost>
                    ) {
                        val id_transaksi = response.body()!!.data.transaksi.id
                        Log.e("id_transaksi", id_transaksi.toString())

                        for(item in my_cart!!) {
                            api.postItemTransaksi(
                                token.toString(),
                                id_transaksi.toInt(),
                                item.id,
                                item.qty,
                                item.harga
                            ).enqueue(
                                object :
                                    Callback<ItemTransaksiResponsePost> {
                                    override fun onResponse(
                                        call: Call<ItemTransaksiResponsePost>,
                                        response: Response<ItemTransaksiResponsePost>
                                    ) {

                                    }

                                    override fun onFailure(
                                        call: Call<ItemTransaksiResponsePost>,
                                        t: Throwable
                                    ) {
                                        Log.e("Error", t.toString())
                                    }
                                })
                        }
                        Toast.makeText(activity?.applicationContext, "Data Transaksi di Simpan", Toast.LENGTH_LONG).show()

                        findNavController().navigate(R.id.transaksiFragment)
                    }

                    override fun onFailure(call: Call<TransaksiResponsePost>, t: Throwable) {
                        Log.e("Error", t.toString())
                    }
                }
            )
        }

        return view
    }

}