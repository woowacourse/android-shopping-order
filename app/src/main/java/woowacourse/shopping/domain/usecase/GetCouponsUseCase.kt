package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.CouponDetailInfo
import woowacourse.shopping.domain.model.CouponDetailInfo.Companion.isAvailable
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CouponRepository

class GetCouponsUseCase(
    private val couponRepository: CouponRepository,
) {
    suspend operator fun invoke(products: Products): Result<List<CouponDetailInfo>> {
        val result = couponRepository.fetchAllCoupons()

        return if (result.isSuccess) {
            val allCoupons = result.getOrThrow()
            Result.success(filterAvailableCoupons(allCoupons, products))
        } else {
            Result.failure(result.exceptionOrNull()!!)
        }
    }

    private fun filterAvailableCoupons(
        coupons: List<CouponDetailInfo>,
        products: Products,
    ): List<CouponDetailInfo> = coupons.filter { it.isAvailable(products) }
}
