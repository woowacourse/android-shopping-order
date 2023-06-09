package woowacourse.shopping.data.discount

import woowacourse.shopping.domain.OrderPriceInfo

data class DiscountInfoDto(
    val discountInformation: List<DiscountResultDto>
) {
    fun toDomain(): OrderPriceInfo {
        return OrderPriceInfo(discountInformation.map(DiscountResultDto::toDomain))
    }
}
