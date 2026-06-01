package woowacourse.shopping.data.repository.payment

import kotlinx.coroutines.CancellationException
import woowacourse.shopping.data.datasource.remote.payment.CouponRemoteDataSource
import woowacourse.shopping.data.mapper.toDomainCoupons
import woowacourse.shopping.domain.repository.CouponRepository

class CouponRepositoryImpl(
    private val couponRemoteDataSource: CouponRemoteDataSource,
) : CouponRepository {
    override suspend fun requestCoupons(): CouponRepository.CouponRequestResult =
        try {
            CouponRepository.CouponRequestResult.Success(
                coupons =
                    couponRemoteDataSource
                        .requestCoupons()
                        .toDomainCoupons(),
            )
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (exception: Exception) {
            CouponRepository.CouponRequestResult.Failure(cause = exception)
        }
}
