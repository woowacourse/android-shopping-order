package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class RecentProductUiModel(
    val productId: Long,
    val imageUrl: String,
    val dateTime: LocalDateTime,
) : Parcelable
