package woowacourse.shopping.model

import java.io.Serializable

data class CartProductUIModel(
    val id: Long,
    val quantity: Int,
    val product: ProductUIModel,
) : Serializable
