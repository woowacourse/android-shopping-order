package woowacourse.shopping.model.product

@JvmInline
value class Money(
    val value: Int,
) : Comparable<Money> {
    init {
        require(value >= 0) { "금액은 0 이상이어야 합니다." }
    }

    operator fun plus(other: Money): Money = Money(value + other.value)

    operator fun minus(other: Money): Money = Money((value - other.value).coerceAtLeast(0))

    operator fun times(quantity: Int): Money = Money(value * quantity)

    override fun compareTo(other: Money): Int = value.compareTo(other.value)

    companion object {
        val ZERO = Money(0)
    }
}
