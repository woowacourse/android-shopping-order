package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CouponRepository

class GetCouponsUseCase(
    private val couponRepository: CouponRepository,
) {
    suspend operator fun invoke(products: Products): Result<List<Coupon>> {
        val result = couponRepository.fetchAllCoupons()

        return if (result.isSuccess) {
            val allCoupons = result.getOrThrow()
            Result.success(filterAvailableCoupons(allCoupons, products))
        } else {
            Result.failure(result.exceptionOrNull()!!)
        }
    }

    private fun filterAvailableCoupons(
        coupons: List<Coupon>,
        products: Products,
    ): List<Coupon> = coupons.filter { it.isAvailable(products) }
}
