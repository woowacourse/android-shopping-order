package woowacourse.shopping.data.remote.dto

data class ProductWithQuantityDTO(val productDTO: ProductDTO?, val quantity: Int?) {
    val isNotNull: Boolean
        get() = productDTO != null && productDTO.isNotNull && quantity != null
}
