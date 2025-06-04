package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Coupons
import woowacourse.shopping.domain.repository.CouponRepository

class GetCouponsUseCase(
    private val repository: CouponRepository,
) {
    suspend operator fun invoke(): Result<Coupons> = repository.fetchAllCoupons()
}
