package woowacourse.shopping.fixture

import woowacourse.shopping.data.model.Coupon
import woowacourse.shopping.data.model.CouponPolicyContext
import woowacourse.shopping.data.model.CouponResponse
import woowacourse.shopping.data.model.CouponType
import woowacourse.shopping.data.util.TimeProvider
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class FakeCouponRepository(
    private val coupons: List<Coupon>,
    private val timeProvider: TimeProvider,
) : CouponRepository {
    override suspend fun getCoupons(
        totalAmount: Long,
        orderProducts: List<ProductUiModel>,
    ): Result<List<Coupon>> {
        val filteredCoupons =
            coupons.filter { coupon ->
                val policy = CouponType.from(coupon.code).getPolicy()
                val policyContext =
                    CouponPolicyContext(
                        totalAmount = totalAmount,
                        orderProducts = orderProducts,
                        currentDateTime = timeProvider.currentTime(),
                    )
                policy.isApplicable(coupon.toResponse(), policyContext)
            }

        return Result.success(filteredCoupons)
    }

    private fun Coupon.toResponse(): CouponResponse {
        return CouponResponse(
            id = this.id,
            description = this.description,
            expirationDate = this.expirationDate,
            code = this.code,
            discount = this.discount,
            minimumAmount = this.minimumAmount,
            discountType = this.discountType,
            buyQuantity = this.buyQuantity,
            getQuantity = this.getQuantity,
            availableTime =
                this.availableTime?.let {
                    CouponResponse.AvailableTime(
                        start = it.start,
                        end = it.end,
                    )
                },
        )
    }
}
