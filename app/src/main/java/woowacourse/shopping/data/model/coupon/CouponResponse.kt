package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
sealed interface CouponResponse {
    val id: Long
    val code: String
    val description: String
    val expirationDate: LocalDate
}
