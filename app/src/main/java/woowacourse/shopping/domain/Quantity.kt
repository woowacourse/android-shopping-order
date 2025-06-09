package woowacourse.shopping.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Quantity(val value: Int): Parcelable {
    fun hasQuantity(minValue: Int = 1): Boolean = value >= minValue

    operator fun plus(other: Int): Quantity {
        return Quantity(this.value + other)
    }

    operator fun minus(other: Int): Quantity {
        return Quantity((this.value - other).coerceAtLeast(0))
    }
}
