package woowacourse.shopping.domain.repository

interface CouponRepository {
    suspend fun obtainCoupons()
}
