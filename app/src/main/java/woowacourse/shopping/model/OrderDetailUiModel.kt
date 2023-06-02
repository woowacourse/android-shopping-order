package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class OrderDetailUiModel(
    val priceBeforeDiscount: Int,
    val priceAfterDiscount: Int,
    val dateTime: LocalDateTime,
    val orderItems: List<OrderProductUiModel>
) : Parcelable
