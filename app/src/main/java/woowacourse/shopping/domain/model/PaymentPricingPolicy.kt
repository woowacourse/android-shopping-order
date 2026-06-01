package woowacourse.shopping.domain.model

class PaymentPricingPolicy(
    val defaultDeliveryPrice: Int = DEFAULT_DELIVERY_PRICE,
    val percentDenominator: Int = PERCENT_DENOMINATOR,
) {
    private companion object {
        private const val DEFAULT_DELIVERY_PRICE = 3000
        private const val PERCENT_DENOMINATOR = 100
    }
}
