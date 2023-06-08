package woowacourse.shopping.data.remote.dto

data class ProductWithCartInfoDTO(val product: ProductDTO?, val cartItem: CartItemDTO?) {
    val isNotNull: Boolean
        get() = product != null && product.isNotNull && (cartItem == null || (cartItem.isNotNull))
    data class CartItemDTO(val id: Int?, val quantity: Int?) {
        val isNotNull: Boolean
            get() = id != null && quantity != null
    }
}
