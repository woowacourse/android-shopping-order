package woowacourse.shopping.data.discount.source

data class NetworkDiscountInfo(
    val discountInformation: List<NetworkDiscountResult>
)

data class NetworkDiscountResult(
    val policyName: String,
    val discountRate: Double,
    val discountPrice: Int
)
