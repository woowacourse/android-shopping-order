package com.example.domain.model

class MoneySalePolicy {
    fun saleApply(cartProducts: List<CartProduct>): Pair<MoneySale, Price> {
        val originPrice =
            cartProducts.filter { it.checked }.sumOf { it.count * it.product.price.value }

        MoneySale.values().forEach {
            if (it.boundary <= originPrice) return Pair(it, Price(originPrice - it.saleAmount))
        }
        return Pair(MoneySale.NONE, Price(originPrice))
    }

    companion object {
        enum class MoneySale(val boundary: Int, val saleAmount: Int) {
            FIVE(50000, 5000),
            THREE(30000, 3000),
            NONE(0, 0),
        }
    }
}
