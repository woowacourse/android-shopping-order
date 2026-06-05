package woowacourse.shopping.model

@JvmInline
value class Money(
    val amount: Long,
) {
    init {
        require(amount >= 0) { "금액은 0원 이상이어야 합니다." }
    }

    operator fun times(time: Int): Money {
        require(time >= 0) { "곱셈 횟수는 0이상이어야 합니다." }

        return Money(Math.multiplyExact(amount, time))
    }

    operator fun plus(money: Money): Money = Money(amount + money.amount)

    operator fun minus(money: Money): Money {
        require(amount >= money.amount) { "기존 금액이 빼는 금액보다 커야합니다." }

        return Money(amount - money.amount)
    }
}
