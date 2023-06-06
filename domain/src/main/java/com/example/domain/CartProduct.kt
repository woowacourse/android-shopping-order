package com.example.domain

class CartProduct(
    val id: Long,
    val productId: Long,
    val productImageUrl: String,
    val productName: String,
    val productPrice: Int,
    var quantity: Int,
    var isPicked: Boolean = true
) {
    companion object {
        const val MIN_COUNT_VALUE = 1
        const val MAX_COUNT_VALUE = 99
    }
}
