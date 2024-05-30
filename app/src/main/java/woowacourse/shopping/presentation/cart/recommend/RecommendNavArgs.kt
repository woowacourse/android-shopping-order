package woowacourse.shopping.presentation.cart.recommend

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.presentation.cart.CartProductUi

@Parcelize
data class RecommendNavArgs(
    val orderProducts: List<CartProductUi>
) : Parcelable