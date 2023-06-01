package woowacourse.shopping.data.order

import woowacourse.shopping.data.entity.CartItemIdsEntity
import woowacourse.shopping.data.entity.DiscountsEntity
import woowacourse.shopping.data.entity.OrderEntity
import woowacourse.shopping.network.retrofit.OrderRetrofitService

class OrderRemoteSource(private val orderService: OrderRetrofitService) : OrderDataSource {
    override fun save(cartItemIds: List<Long>, userToken: String): Result<Long> {
        val response =
            orderService.postOrder("Basic $userToken", CartItemIdsEntity(cartItemIds)).execute()
        return response.runCatching {
            if (code() != 201) throw Throwable(message())
            headers()["Location"]?.substringAfterLast("/")?.toLong() ?: throw Throwable(message())
        }
    }

    override fun findById(id: Long, userToken: String): Result<OrderEntity> {
        val response = orderService.selectOrderById("Basic $userToken", id).execute()
        return response.runCatching {
            if (code() != 200) throw Throwable(message())
            body() ?: throw Throwable(message())
        }
    }

    override fun findAll(userToken: String): Result<List<OrderEntity>> {
        val response = orderService.selectOrders("Basic $userToken").execute()
        return response.runCatching {
            if (code() != 200) throw Throwable(message())
            body() ?: throw Throwable(message())
        }
    }

    override fun findDiscountPolicy(price: Int, memberGrade: String): Result<DiscountsEntity> {
        val response = orderService.selectDiscountPolicy(price, memberGrade).execute()
        return response.runCatching {
            if (code() != 200) throw Throwable(message())
            body() ?: throw Throwable(message())
        }
    }
}
