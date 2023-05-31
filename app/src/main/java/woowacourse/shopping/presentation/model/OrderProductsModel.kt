package woowacourse.shopping.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderProductsModel(val list: List<OrderProductModel>) : Parcelable
