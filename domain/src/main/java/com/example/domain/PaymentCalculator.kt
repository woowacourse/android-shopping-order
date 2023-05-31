package com.example.domain

class PaymentCalculator {
    fun totalPaymentAmount(cartProducts: List<CartProduct>): Int {
        val countableCartProducts: List<CartProduct> =
            cartProducts.filter { it.isPicked && 0 < it.quantity }
        var amount = 0

        countableCartProducts.forEach {
            amount += it.productPrice * it.quantity
        }

        return amount
    }
}
