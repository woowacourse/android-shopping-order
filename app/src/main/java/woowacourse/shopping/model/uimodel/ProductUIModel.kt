package woowacourse.shopping.model.uimodel

import java.io.Serializable

class ProductUIModel(
    val id: Long,
    val name: String,
    val url: String,
    val price: Int,
) : Serializable
