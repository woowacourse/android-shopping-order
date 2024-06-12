package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.model.remote.CouponDto

interface CouponDataSource {
    suspend fun getCoupons(): Result<List<CouponDto>>

    companion object {
        private var instance: CouponDataSource? = null

        fun setInstance(orderDataSource: CouponDataSource) {
            instance = orderDataSource
        }

        fun getInstance(): CouponDataSource = requireNotNull(instance)
    }
}
