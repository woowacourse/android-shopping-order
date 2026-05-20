package woowacourse.shopping.domain.model.product

@JvmInline
value class Price(
    val value: Int,
) {
    init {
        require(value >= 0) { "가격이 음수입니다." }
    }
}
