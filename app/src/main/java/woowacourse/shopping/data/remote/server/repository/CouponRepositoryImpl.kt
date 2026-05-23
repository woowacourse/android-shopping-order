package woowacourse.shopping.data.remote.server.repository

import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.dto.coupon.item.toDomain
import woowacourse.shopping.data.remote.server.service.CouponService
import woowacourse.shopping.domain.coupon.Coupon

class CouponRepositoryImpl(
    private val couponService: CouponService
) : CouponRepository {
    override suspend fun getCoupons(): ApiResult<List<Coupon>> =
        try {
            val response = couponService.getCoupons()
            ApiResult.Success(response.map { it.toDomain() })
        } catch (e: HttpException) {
            ApiResult.Error(e.code(), e.message)
        } catch (e: Exception) {
            ApiResult.Exception(e)
        } catch (e: CancellationException) {
            throw e
        }
}
