package woowacourse.shopping.presentation.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartProductListModel(val items: List<CartProductModel>) : Parcelable
