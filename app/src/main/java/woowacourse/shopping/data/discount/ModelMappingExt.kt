package woowacourse.shopping.data.discount

import woowacourse.shopping.data.discount.source.NetworkDiscountInfo
import woowacourse.shopping.data.discount.source.NetworkDiscountResult

fun String.toExternal(): DiscountPolicy = when (this) {
    "memberGradeDiscount" -> DiscountPolicy.MEMBER_GRADE
    "priceDiscount" -> DiscountPolicy.PRICE
    else -> throw IllegalArgumentException("정의되지 않은 할인 정책명입니다.")
}

fun NetworkDiscountResult.toExternal() = DiscountResult(
    policyName.toExternal(),
    discountRate,
    discountPrice
)

fun List<NetworkDiscountResult>.toExternal() = map(NetworkDiscountResult::toExternal)

fun NetworkDiscountInfo.toExternal() = DiscountInfo(
    discountInformation.toExternal()
)
