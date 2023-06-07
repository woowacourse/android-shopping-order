package woowacourse.shopping.ui.model

import java.io.Serializable

data class OrderProductUiModel(
    val id: Int,
    val name: String,
    val count: Int,
    val price: Int,
    val imageUrl: String,
) : Serializable
