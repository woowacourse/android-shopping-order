package woowacourse.shopping.ui.model

import java.io.Serializable

data class OrderUiModel(
    val id: Int,
    val date: String?,
    val products: List<OrderProductUiModel>,
    val totalPrice: Int,
    val usedPoint: Int,
    val earnedPoint: Int,
) : Serializable
