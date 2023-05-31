package com.example.domain.model

interface SalePolicy {
    fun saleApply(cartProducts: List<CartProduct>): Price
}
