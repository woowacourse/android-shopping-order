package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Coupons
import woowacourse.shopping.domain.repository.CouponRepository

class GetCouponsUseCase(
    private val repository: CouponRepository,
) {
    suspend operator fun invoke(): Result<Coupons> {
        val coupons =
            repository.fetchAllCoupons().getOrElse {
                return Result.failure(Throwable("[GetCouponsUseCase] 쿠폰 목록 불러오기 오류", it))
            }

        return Result.success(Coupons(coupons))
    }
}
