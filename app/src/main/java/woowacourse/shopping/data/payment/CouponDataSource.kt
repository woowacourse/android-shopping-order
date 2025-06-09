package woowacourse.shopping.data.payment

interface CouponDataSource {
    suspend fun loadCoupons(): List<CouponDataModel>
}
