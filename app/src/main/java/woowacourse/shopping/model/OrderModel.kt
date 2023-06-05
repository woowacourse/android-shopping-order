package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.R
import woowacourse.shopping.ui.order.recyclerview.ListItem

@Parcelize
data class OrderModel(
    val id: Int = DEFAULT_ORDER_ID,
    val orderProducts: List<OrderProductModel>,
    val payment: PaymentModel,
) : Parcelable, ListItem {

    @IgnoredOnParcel
    override val viewType: Int = R.layout.item_order_history

    @IgnoredOnParcel
    val firstOrderProductName: String? = orderProducts.firstOrNull()?.name

    @IgnoredOnParcel
    val firstProductQuantity: ProductCountModel =
        orderProducts.firstOrNull()?.quantity ?: ProductCountModel(0)

    @IgnoredOnParcel
    val firstProductTotalPayment: PriceModel =
        orderProducts.firstOrNull()?.totalPrice ?: PriceModel(0)

    @IgnoredOnParcel
    val thumbnailUrl: String? = orderProducts.firstOrNull()?.imageUrl

    @IgnoredOnParcel
    val orderProductSize: Int = orderProducts.size

    companion object {
        private const val DEFAULT_ORDER_ID = -1
    }
}
