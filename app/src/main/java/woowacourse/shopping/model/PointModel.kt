package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.ui.order.recyclerview.ListItem
import woowacourse.shopping.ui.order.recyclerview.OrderViewType

@Parcelize
data class PointModel(
    val value: Int,
) : Parcelable, ListItem {
    @IgnoredOnParcel
    override val viewType: Int = OrderViewType.POINT.value
}
