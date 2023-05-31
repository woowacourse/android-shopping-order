package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class RecentProductUiModel(
    val product: ProductUiModel,
    val dateTime: LocalDateTime,
) : Parcelable