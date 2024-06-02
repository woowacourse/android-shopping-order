package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.domain.model.CartDomain
import woowacourse.shopping.domain.model.CartItemDomain

data class CartResponse(
    @SerializedName("content")
    val cartItems: List<CartItem>,
    @SerializedName("empty")
    val empty: Boolean,
    @SerializedName("first")
    val first: Boolean,
    @SerializedName("last")
    val last: Boolean,
    @SerializedName("number")
    val number: Int,
    @SerializedName("numberOfElements")
    val numberOfElements: Int,
    @SerializedName("pageable")
    val pageable: Pageable,
    @SerializedName("size")
    val size: Int,
    @SerializedName("sort")
    val sort: Sort,
    @SerializedName("totalElements")
    val totalElements: Int,
    @SerializedName("totalPages")
    val totalPages: Int,
)

fun CartResponse.toCartDomain(): CartDomain =
    CartDomain(
        cartItems = cartItems.map(CartItem::toCartItemDomain)
    )

fun CartItem.toCartItemDomain(): CartItemDomain =
    CartItemDomain(
        cartItemId = cartItemId,
        quantity = quantity,
        product = product.toProductItemDomain()
    )
