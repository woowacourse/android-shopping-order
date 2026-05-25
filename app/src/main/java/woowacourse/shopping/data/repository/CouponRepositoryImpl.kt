package woowacourse.shopping.data.repository

import woowacourse.shopping.data.remote.api.CouponApi
import woowacourse.shopping.data.remote.dto.response.coupon.AvailableTimeResponse
import woowacourse.shopping.data.remote.dto.response.coupon.CouponResponse
import woowacourse.shopping.model.AvailableTime
import woowacourse.shopping.model.Coupon
import woowacourse.shopping.model.DiscountType
import kotlin.coroutines.cancellation.CancellationException

class CouponRepositoryImpl(
    private val couponApi: CouponApi,
) : CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> =
        try {
            Result.success(couponApi.getCoupons().map { it.toDomain() })
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    private fun CouponResponse.toDomain(): Coupon =
        Coupon(
            id = id,
            code = code,
            description = description,
            expirationDate = expirationDate,
            discount = discount,
            minimumAmount = minimumAmount,
            availableTime = availableTime?.toDomain(),
            buyQuantity = buyQuantity,
            getQuantity = getQuantity,
            discountType = discountType.toDiscountType(),
        )

    private fun AvailableTimeResponse.toDomain(): AvailableTime =
        AvailableTime(
            start = start,
            end = end,
        )

    private fun String.toDiscountType(): DiscountType =
        when (this) {
            "fixed" -> DiscountType.FIXED
            "percentage" -> DiscountType.PERCENTAGE
            "buyXgetY" -> DiscountType.BUY_X_GET_Y
            "freeShipping" -> DiscountType.FREE_SHIPPING
            else -> DiscountType.FIXED
        }
}
