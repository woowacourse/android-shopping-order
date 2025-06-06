package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.CachedCartItem
import woowacourse.shopping.data.model.Coupon
import woowacourse.shopping.data.model.Coupon.AvailableTime
import woowacourse.shopping.data.source.remote.payment.PaymentDataSource
import woowacourse.shopping.domain.model.PagingData
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    val paymentDataSource: PaymentDataSource
): CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> {
        val result = paymentDataSource.getCoupons()

        return result.mapCatching { responseList ->
            responseList.map { response ->
                Coupon(
                    id = response.id,
                    code = response.code,
                    description = response.description,
                    expirationDate = response.expirationDate,
                    discountType = response.discountType,
                    discount = response.discount,
                    buyQuantity = response.buyQuantity,
                    getQuantity = response.getQuantity,
                    minimumAmount = response.minimumAmount,
                    availableTime = response.availableTime?.let {
                        AvailableTime(
                            start = it.start,
                            end = it.end
                        )
                    }
                )
            }
        }
    }
}
