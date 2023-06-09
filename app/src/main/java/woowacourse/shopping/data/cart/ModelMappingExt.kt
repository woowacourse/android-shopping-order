package woowacourse.shopping.data.cart

import woowacourse.shopping.data.cart.source.NetworkCartItem
import woowacourse.shopping.data.product.toExternal

fun NetworkCartItem.toExternal() = CartItem(
    id,
    product.toExternal(),
    quantity
)

fun List<NetworkCartItem>.toExternal() = map(NetworkCartItem::toExternal)
