package woowacourse.shopping.domain

import java.io.Serializable

@JvmInline
value class Quantity(val value: Int) : Serializable {
    fun hasQuantity(minValue: Int = 1): Boolean = value >= minValue

    operator fun plus(other: Int): Quantity {
        return Quantity(this.value + other)
    }

    operator fun minus(other: Int): Quantity {
        return Quantity((this.value - other).coerceAtLeast(0))
    }
}
