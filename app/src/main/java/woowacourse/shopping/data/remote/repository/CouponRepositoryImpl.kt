package woowacourse.shopping.data.remote.repository

import woowacourse.shopping.data.remote.datasource.coupon.CouponDataSource
import woowacourse.shopping.data.remote.dto.mapper.toDomain
import woowacourse.shopping.domain.Coupon
import woowacourse.shopping.domain.CouponRepository

class CouponRepositoryImpl(
    private val couponDataSource: CouponDataSource,
) : CouponRepository {
    override suspend fun getCoupons(): Result<List<Coupon>> =
        runCatching {
            val response = couponDataSource.getCoupons()
            if (response.isSuccessful) {
                return Result.success(response.body()?.map { it.toDomain() } ?: emptyList())
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }.onFailure {
        }
}
