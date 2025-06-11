package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.remote.payment.PaymentDataSource
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.policy.CouponEvaluator
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.mapper.toDomain

class CouponRepositoryImpl(
    private val couponEvaluator: CouponEvaluator,
    val paymentDataSource: PaymentDataSource,
) : CouponRepository {
    override suspend fun getCoupons(
        totalAmount: Long,
        orderProducts: List<Product>,
    ): Result<List<Coupon>> {
        val result = paymentDataSource.getCoupons()

        return result.mapCatching { responseList ->
            val domainCoupons = responseList.map { it.toDomain() }

            couponEvaluator.evaluateApplicableCoupons(
                coupons = domainCoupons,
                totalAmount = totalAmount,
                orderProducts = orderProducts,
            )
        }
    }
}
