package woowacourse.shopping.common.model

import java.io.Serializable

data class CartProductModel(
    val id: Int,
    val quantity: Int,
    val isChecked: Boolean,
    val product: ProductModel
) : Serializable
