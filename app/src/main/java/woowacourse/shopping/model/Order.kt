package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.R
import woowacourse.shopping.ui.order.recyclerview.ListItem

typealias UiOrder = Order

@Parcelize
data class Order(
    val id: Int = DEFAULT_ORDER_ID,
    val orderProducts: List<OrderProduct>,
    val totalPayment: Price,
) : Parcelable, ListItem {
    @IgnoredOnParcel
    override val viewType: Int = R.layout.item_order_history

    @IgnoredOnParcel
    val firstOrderProductName: String? = orderProducts.firstOrNull()?.name

    @IgnoredOnParcel
    val firstProductQuantity: UiProductCount =
        orderProducts.firstOrNull()?.quantity ?: UiProductCount(0)

    @IgnoredOnParcel
    val firstProductTotalPayment: UiPrice = orderProducts.firstOrNull()?.totalPrice ?: UiPrice(0)

    @IgnoredOnParcel
    val thumbnailUrl: String? = orderProducts.firstOrNull()?.imageUrl

    @IgnoredOnParcel
    val orderProductSize: Int = orderProducts.size

    companion object {
        private const val DEFAULT_ORDER_ID = -1
    }
}
