package woowacourse.shopping.domain.model

data class Payment(
    val originalPayment: Int,
    val finalPayment: Int,
    val point: Int,
)
