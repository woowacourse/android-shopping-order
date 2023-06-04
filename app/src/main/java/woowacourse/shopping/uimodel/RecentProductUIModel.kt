package woowacourse.shopping.uimodel

import java.io.Serializable

data class RecentProductUIModel(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
) : Serializable