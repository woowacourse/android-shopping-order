package com.example.domain.model

data class Cart(
    val items: List<CartItem>,
) {
    val price = items.sumOf { it.price }

    fun contains(condition: (CartItem) -> Boolean): Boolean =
        items.firstOrNull {
            condition(it)
        } != null

    fun filter(condition: (CartItem) -> Boolean): Cart =
        Cart(
            items.filter {
                condition(it)
            },
        )

    fun mostExpensiveItem(): CartItem = items.maxBy { it.price }
}
