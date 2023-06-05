package woowacourse.shopping.model

import android.os.Parcelable
import android.view.View.GONE
import android.view.View.VISIBLE
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductCountModel(val value: Int) : Parcelable {
    override fun toString(): String =
        if (value > 99) "99" else value.toString()

    fun getVisibility(): Int =
        if (value > 0) VISIBLE else GONE
}
