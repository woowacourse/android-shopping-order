package woowacourse.shopping.data.remote

import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.data.entity.CartItemIdsEntity
import woowacourse.shopping.data.entity.DiscountsEntity
import woowacourse.shopping.data.entity.OrderEntity
import woowacourse.shopping.data.remote.retrofit.OrderRetrofitService
import woowacourse.shopping.error.DataError

class OrderRemoteSource(private val orderService: OrderRetrofitService) : OrderDataSource {
    override fun save(cartItemIds: List<Long>, userToken: String): Result<Long> {
        return runCatching {
            val response =
                orderService.postOrder(
                    Authorization.KEY_FORMAT.format(userToken),
                    CartItemIdsEntity(cartItemIds)
                ).execute()
            if (response.code() != 201) throw DataError.OrderSaveError(response.message())
            response.headers()["Location"]?.substringAfterLast("/")?.toLong()
                ?: throw DataError.OrderSaveError(
                    response.message()
                )
        }
    }

    override fun findById(id: Long, userToken: String): Result<OrderEntity> {
        return runCatching {
            val response =
                orderService.selectOrderById(Authorization.KEY_FORMAT.format(userToken), id)
                    .execute()
            if (response.code() != 200) throw DataError.OrderFindError(response.message())
            response.body() ?: throw DataError.OrderFindError(response.message())
        }
    }

    override fun findAll(userToken: String): Result<List<OrderEntity>> {
        return runCatching {
            val response =
                orderService.selectOrders(Authorization.KEY_FORMAT.format(userToken)).execute()
            if (response.code() != 200) throw DataError.OrderFindError(response.message())
            response.body() ?: throw DataError.OrderFindError(response.message())
        }
    }

    override fun findDiscountPolicy(price: Int, memberGrade: String): Result<DiscountsEntity> {
        return runCatching {
            val response = orderService.selectDiscountPolicy(price, memberGrade).execute()
            if (response.code() != 200) throw DataError.OrderFindError(response.message())
            response.body() ?: throw DataError.OrderFindError(response.message())
        }
    }
}
