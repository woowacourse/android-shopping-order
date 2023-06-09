package com.example.domain.cart

import com.example.domain.Product

data class CartProduct(
    val id: Long,
    val product: Product,
    val count: Int = 0,
    val checked: Boolean,
) {
    fun updateChecked(isChecked: Boolean): CartProduct {
        return copy(checked = isChecked)
    }

    fun updateCount(count: Int): CartProduct {
        return copy(count = count)
    }

    fun increaseCount(): CartProduct {
        return copy(count = this.count + 1)
    }

    fun decreaseCount(): CartProduct {
        return copy(count = this.count - 1)
    }
}
