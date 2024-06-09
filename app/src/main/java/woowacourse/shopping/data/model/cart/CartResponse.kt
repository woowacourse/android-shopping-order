package woowacourse.shopping.data.model.cart

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.data.model.Pageable
import woowacourse.shopping.data.model.Sort
import woowacourse.shopping.data.model.product.toProductItemDomain
import woowacourse.shopping.domain.model.CartData
import woowacourse.shopping.domain.model.CartDomain
import woowacourse.shopping.domain.model.CartItemDomain

data class CartResponse(
    @SerializedName("content")
    val cartItems: List<CartItem>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: Sort,
    val totalElements: Int,
    val totalPages: Int,
)

fun CartResponse.toCartDomain(): CartDomain =
    CartDomain(
        cartItems = cartItems.map(CartItem::toCartItemDomain),
        empty = empty,
        first = first,
        last = last,
        totalPages = totalPages,
    )

fun CartItem.toCartItemDomain(): CartItemDomain =
    CartItemDomain(
        cartItemId = cartItemId,
        quantity = quantity,
        product = product.toProductItemDomain(),
    )

fun CartItem.toCartData(): CartData =
    CartData(
        cartItemId = cartItemId,
        productId = product.id,
        quantity = quantity,
    )
