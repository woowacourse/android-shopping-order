package woowacourse.shopping.data.shoppingCart.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct

@Serializable
data class ShoppingCartItemsResponseDto(
    @SerialName("content")
    val shoppingCartItems: List<ShoppingCartItemResponseDto>,
    @SerialName("empty")
    val empty: Boolean,
    @SerialName("first")
    val first: Boolean,
    @SerialName("last")
    val last: Boolean,
    @SerialName("number")
    val number: Int,
    @SerialName("numberOfElements")
    val numberOfElements: Int,
    @SerialName("pageable")
    val pageable: PageableResponseDto,
    @SerialName("size")
    val size: Int,
    @SerialName("sort")
    val sort: SortResponseDto,
    @SerialName("totalElements")
    val totalElements: Int,
    @SerialName("totalPages")
    val totalPages: Int,
) {
    fun toDomain(): List<ShoppingCartProduct> = shoppingCartItems.map(ShoppingCartItemResponseDto::toDomain)
}
