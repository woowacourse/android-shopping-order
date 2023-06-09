package woowacourse.shopping.presentation.model

data class OrderModel(
    val id: Long,
    val cartProducts: CartProductsModel,
    val usePoint: PointModel,
    val card: CardModel,
)
