package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class OrderMinInfoItemUiModel(
    val id: Long,
    val mainProductName: String,
    val mainProductImage: String,
    val extraProductCount: Int,
    val date: LocalDateTime,
    val price: Int
) : Parcelable
