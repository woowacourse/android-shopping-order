package woowacourse.shopping.data.discount.source

import woowacourse.shopping.utils.UserData

class DefaultNetworkDiscountInfoDataSource(
    private val discountInfoRemoteService: DiscountInfoRemoteService
) : NetworkDiscountInfoDataSource {
    override fun loadDiscountInfo(price: Int): Result<NetworkDiscountInfo> {
        return kotlin.runCatching {
            val response = discountInfoRemoteService.requestDiscountInfo(price, UserData.grade)
                .execute()
            if (response.isSuccessful.not()) throw Throwable(response.errorBody()?.string())
            response.body() ?: throw Throwable(response.message())
        }
    }
}
