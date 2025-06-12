package woowacourse.shopping.data.coupon.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.coupon.dto.CouponResponseItem
import woowacourse.shopping.data.coupon.source.CouponDataSource
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.di.DataSourceModule
import woowacourse.shopping.domain.coupon.Coupon

class DefaultCouponRepository(
    private val couponDataSource: CouponDataSource = DataSourceModule.remoteCouponDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CouponRepository {
    override suspend fun loadCoupons(): List<Coupon> = withContext(ioDispatcher) {
        val couponResponse: List<CouponResponseItem> = couponDataSource.coupons()
        couponResponse.toDomain()
    }

    private fun List<CouponResponseItem>.toDomain(): List<Coupon> {
        return this.mapNotNull { item ->
            when (item) {
                is CouponResponseItem.FixedDiscountCoupon -> item.toDomain()
                is CouponResponseItem.PercentageCoupon -> item.toDomain()
                is CouponResponseItem.BuyXGetYCoupon -> item.toDomain()
                is CouponResponseItem.FreeShippingCoupon -> item.toDomain()
            }
        }
    }
}