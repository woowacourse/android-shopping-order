package com.example.domain.model

data class Receipt(
    val price: Int,
    val discountAmount: Int,
    val shippingFee: Int,
) {
    val finalPrice = price - discountAmount + shippingFee
}
