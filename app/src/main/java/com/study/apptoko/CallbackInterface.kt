package com.study.apptoko

import com.study.apptoko.response.cart.Cart

interface CallbackInterface {
    fun passResultCallback(total:String, cart: ArrayList<Cart>)
}