package com.example.domain

class CartProduct(
    val id: Int,
    val productId: Int,
    val productImageUrl: String,
    val productName: String,
    val productPrice: Int,
    val quantity: Int,
    val isPicked: Boolean = true
) {
    companion object {
        const val MIN_COUNT_VALUE = 1
        const val MAX_COUNT_VALUE = 99
    }
}
