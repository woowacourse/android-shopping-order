package woowacourse.shopping.presentation.model

data class CartProductInfoModel(
    val id: Int,
    val productModel: ProductModel,
    val count: Int,
    val isOrdered: Boolean = false,
)
