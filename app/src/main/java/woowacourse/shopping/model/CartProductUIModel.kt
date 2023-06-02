package woowacourse.shopping.model

data class CartProductUIModel(
    val id: Long,
    val quantity: Int,
    val product: ProductUIModel,
)
