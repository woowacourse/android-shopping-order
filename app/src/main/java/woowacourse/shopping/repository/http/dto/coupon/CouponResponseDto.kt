package woowacourse.shopping.repository.http.dto.coupon

import kotlinx.serialization.Serializable

@Serializable
sealed class CouponResponseDto {
    abstract val id: Long
    abstract val code: String
    abstract val description: String
    abstract val expirationDate: String
    abstract val discountType: String
}
