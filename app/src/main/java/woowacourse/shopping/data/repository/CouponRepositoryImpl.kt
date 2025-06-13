package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.coupon.CouponRemoteDataSource
import woowacourse.shopping.data.di.ApiResult
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val dataSource: CouponRemoteDataSource,
    private var cachedCoupons: List<Coupon> = emptyList(),
) : CouponRepository {
    override suspend fun fetchAllCoupons(): Result<List<Coupon>> =
        when (val apiResult = dataSource.fetchAllCoupons()) {
            is ApiResult.Success ->
                runCatching {
                    val allCoupons = apiResult.data.map { it.toDomain() }
                    cachedCoupons = allCoupons
                    allCoupons
                }

            is ApiResult.ClientError ->
                Result.failure(Exception("Client error: ${apiResult.code} ${apiResult.message}"))

            is ApiResult.ServerError ->
                Result.failure(Exception("Server error: ${apiResult.code} ${apiResult.message}"))

            is ApiResult.NetworkError ->
                Result.failure(apiResult.throwable)

            ApiResult.UnknownError ->
                Result.failure(Exception("Unknown error"))
        }

    override fun fetchCoupon(couponId: Long): Coupon? = cachedCoupons.find { it.id == couponId }
}
