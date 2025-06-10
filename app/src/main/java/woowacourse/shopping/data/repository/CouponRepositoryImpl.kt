package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.CouponResponse
import woowacourse.shopping.data.source.remote.payment.PaymentDataSource
import woowacourse.shopping.data.util.TimeProvider
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.CouponPolicyContext
import woowacourse.shopping.domain.model.CouponType
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.mapper.toDomain

class CouponRepositoryImpl(
    private val timeProvider: TimeProvider,
    val paymentDataSource: PaymentDataSource,
) : CouponRepository {
    override suspend fun getCoupons(
        totalAmount: Long,
        orderProducts: List<Product>,
    ): Result<List<Coupon>> {
        val result = paymentDataSource.getCoupons()

        return result.mapCatching { responseList ->
            responseList
                .filter { coupon ->
                    isCouponApplicable(coupon, totalAmount, orderProducts)
                }.map { response ->
                    response.toDomain()
                }
        }
    }

    private fun isCouponApplicable(
        coupon: CouponResponse,
        totalAmount: Long,
        orderProducts: List<Product>,
    ): Boolean {
        val currentDateTime = timeProvider.currentTime()
        val policy = CouponType.from(coupon.code).getPolicy()

        val policyContext =
            CouponPolicyContext(
                totalAmount = totalAmount,
                orderProducts = orderProducts,
                currentDateTime = currentDateTime,
            )
        return policy.isApplicable(coupon, policyContext)
    }
}
