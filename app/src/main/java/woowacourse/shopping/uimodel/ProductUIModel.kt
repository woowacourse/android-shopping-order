package woowacourse.shopping.uimodel

import java.io.Serializable

data class ProductUIModel(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
) : Serializable