package woowacourse.shopping.model

data class OrderDetailProductUiModel(
    override val count: Int,
    override val productUiModel: ProductUiModel
) : OrderProduct
