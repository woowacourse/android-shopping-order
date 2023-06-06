package woowacourse.shopping.data.remote.dto

class CartProductDTO(val id: Int?, val quantity: Int?, val product: ProductDTO?) {
    val isNotNull: Boolean
        get() = id != null && quantity != null && product != null && product.isNotNull
}
