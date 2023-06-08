package woowacourse.shopping.domain.model

data class ProductWithCartInfo(val product: Product, val cartItem: CartItem?) {
    data class CartItem(val id: Int, val quantity: Int)
}
