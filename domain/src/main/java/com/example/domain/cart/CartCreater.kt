package com.example.domain.cart

/**
 * create Cart instance for test
 */
fun createCart(
    products: List<CartProduct> = listOf(createCartProduct())
): Cart {
    return Cart(products)
}

/**
 * create CartProduct instance for test
 */
fun createCartProduct(
    id: Long = 0,
    productId: Long = 0,
    productImageUrl: String = "",
    productName: String = "test cart product",
    productPrice: Int = 1_000,
    quantity: Int = 1,
    isPicked: Boolean = true
): CartProduct {
    return CartProduct(
        id = id, productId = productId, productImageUrl = productImageUrl,
        productName = productName, productPrice = productPrice,
        quantity = quantity, isPicked = isPicked
    )
}
