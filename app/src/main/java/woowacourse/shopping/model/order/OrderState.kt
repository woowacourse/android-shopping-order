package woowacourse.shopping.model.order

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class OrderState(
    val id: Long,
    val originalPrice: Int,
    val discountPrice: Int,
    val finalPrice: Int,
    val orderDate: String,
    val orderProducts: List<OrderProductState>
) : Parcelable
