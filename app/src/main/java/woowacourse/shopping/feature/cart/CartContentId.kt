package woowacourse.shopping.feature.cart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartContentId(
    val id: String,
) : Parcelable
