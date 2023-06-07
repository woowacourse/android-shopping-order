package woowacourse.shopping

data class OrderProduct(val product: Product, val count: Int) {
    val totalPrice: Int = product.price.value * count
}
