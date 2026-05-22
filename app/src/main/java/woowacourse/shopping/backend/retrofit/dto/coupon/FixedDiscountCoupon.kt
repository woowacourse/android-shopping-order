package woowacourse.shopping.backend.retrofit.dto.coupon

import kotlinx.serialization.Serializable

@Serializable
data class ExpirationDate(
    val year: Int,
    val month: Int,
    val day: Int
)

@Serializable
data class FixedDiscountCouponResponse(
    val id: Long?, // 쿠폰을 구문하기 위한 id
    val code: String, // 쿠폰 코드
    val description: String, // 쿠폰 설명
    val expirationDate: String?, // 만료일
    val discount: Int?, // 할인되는 가격
    val minimumAmount: Int?, // 최소 주문 금액
    val discountType: String?, // 할인 방식
)