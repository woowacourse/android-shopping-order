package woowacourse.shopping.data.repository.coupon

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import woowacourse.shopping.data.remote.api.CouponApi
import woowacourse.shopping.data.remote.dto.response.coupon.AvailableTimeResponse
import woowacourse.shopping.data.remote.dto.response.coupon.CouponResponse
import woowacourse.shopping.model.AvailableTime
import woowacourse.shopping.model.Coupon
import woowacourse.shopping.model.Money
import java.time.LocalDate
import java.time.LocalTime

class CouponRepositoryImpl(
    private val api: CouponApi,
) : CouponRepository {
    private val _coupons = MutableStateFlow<List<Coupon>>(emptyList())
    override val coupons: StateFlow<List<Coupon>> = _coupons.asStateFlow()

    override suspend fun refreshCoupons() {
        _coupons.value = api.getCoupons().map { it.toDomain() }
    }

    private fun CouponResponse.toDomain(): Coupon {
        val expirationDate = LocalDate.parse(expirationDate)

        return when (discountType) {
            FIXED ->
                Coupon.Fixed(
                    id = id.toString(),
                    code = code,
                    description = description,
                    expirationDate = expirationDate,
                    discount = Money(requireNotNull(discount).toLong()),
                    minimumAmount = Money(requireNotNull(minimumAmount).toLong()),
                )

            PERCENTAGE ->
                Coupon.Percentage(
                    id = id.toString(),
                    code = code,
                    description = description,
                    expirationDate = expirationDate,
                    discountPercent = requireNotNull(discount),
                    availableTime = requireNotNull(availableTime).toDomain(),
                )

            BUY_X_GET_Y ->
                Coupon.BuyXGetY(
                    id = id.toString(),
                    code = code,
                    description = description,
                    expirationDate = expirationDate,
                    buyQuantity = requireNotNull(buyQuantity),
                    getQuantity = requireNotNull(getQuantity),
                )

            FREE_SHIPPING ->
                Coupon.FreeShipping(
                    id = id.toString(),
                    code = code,
                    description = description,
                    expirationDate = expirationDate,
                    minimumAmount = Money(requireNotNull(minimumAmount).toLong()),
                )

            else -> throw IllegalArgumentException("지원하지 않는 쿠폰입니다.")
        }
    }

    private fun AvailableTimeResponse.toDomain(): AvailableTime =
        AvailableTime(
            start = LocalTime.parse(start),
            end = LocalTime.parse(end),
        )

    companion object {
        private const val FIXED = "fixed"
        private const val PERCENTAGE = "percentage"
        private const val BUY_X_GET_Y = "buyXgetY"
        private const val FREE_SHIPPING = "freeShipping"
    }
}
