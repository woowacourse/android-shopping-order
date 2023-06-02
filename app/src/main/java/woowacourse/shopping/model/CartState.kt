package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CartState(val products: List<CartProductState>) : Parcelable
