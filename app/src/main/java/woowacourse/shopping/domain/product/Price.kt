package woowacourse.shopping.domain.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Price(val value: Int): Parcelable {
    init {
        require(value >= 0) { INVALID_VALUE }
    }

    companion object {
        private const val INVALID_VALUE = "가격은 0원 이상이다"
    }
}
