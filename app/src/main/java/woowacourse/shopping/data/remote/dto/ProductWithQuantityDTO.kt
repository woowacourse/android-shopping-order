package woowacourse.shopping.data.remote.dto

data class ProductWithQuantityDTO(val product: ProductDTO?, val quantity: Int?) {
    val isNotNull: Boolean
        get() = product != null && product.isNotNull && quantity != null
}
