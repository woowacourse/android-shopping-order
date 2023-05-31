package com.example.domain.model

class MoneySalePolicy : SalePolicy {
    override fun saleApply(cartProducts: List<CartProduct>): Price {
        val originPrice =
            cartProducts.filter { it.checked }.sumOf { it.count * it.product.price.value }

        MoneySale.values().forEach {
            if (it.boundary <= originPrice) return Price(originPrice - it.saleAmount)
        }
        return Price(originPrice)
    }

    companion object {
        private enum class MoneySale(val boundary: Int, val saleAmount: Int) {
            FIVE(50000, 5000),
            THREE(30000, 3000)
        }
    }
}
