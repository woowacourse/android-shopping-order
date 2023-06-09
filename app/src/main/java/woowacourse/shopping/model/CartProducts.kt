package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

typealias UiCartProducts = CartProducts

@Parcelize
data class CartProducts(
    val items: List<UiCartProduct>,
) : Parcelable
