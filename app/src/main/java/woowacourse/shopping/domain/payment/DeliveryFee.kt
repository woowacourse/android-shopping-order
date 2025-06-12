package woowacourse.shopping.domain.payment

interface DeliveryFee {
    val value: Int
}

class DefaultDeliveryFee : DeliveryFee {
    override val value: Int = 3_000
}
