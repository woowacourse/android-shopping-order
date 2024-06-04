package woowacourse.shopping

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartItemCounter
import woowacourse.shopping.domain.model.CartItemResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.UpdateCartItemResult
import woowacourse.shopping.domain.model.UpdateCartItemType
import woowacourse.shopping.domain.repository.ShoppingCartRepository

class MockShoppingCartRepository : ShoppingCartRepository {
    val cartItems =
        mutableListOf(
            CartItem(
                id = 0L,
                product =
                    Product(
                        id = 0L,
                        imageUrl =
                            """
                            https://s3-alpha-sig.figma.com/img/6a52/2b6a/05b81120d274b875b55d6da04de4749e?Expires=1716768000&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=bF~PKj~RVd3Rg-U5LFz7izxp92X8QuDAiYxB6S4iXS41nq6BXyKGCmyZdn39WHzQIeIscToVmlFB7WwRKhnfDR-WxMbd7dTuJIRhwiR8iQ5lq6LUm7MLNEv9l779WDsICq0kUJp1MUPVJFDG72HKGqMJaL5KuqlmJeOhlT2Qy1rneIXyjuILXnqAbS56t3YlIPIPTI6BWe3Sk6j4zCPL49M0NNbkTY4bESgBSkqIzfXHTngQyHKXXbn~gM7IQSIumumWVSxY8j3Ms2q813-NrE7J0D1EMRQokCmMQDTnaIzioYiDgIAnoBwurFdR6Ehl~VJfS55vWo50ajYaaGKPMQ__
                            """.trimIndent(),
                        price = 10_000,
                        name = "PET보틀-단지(400ml) 레몬청",
                        category = "",
                    ),
            ),
            CartItem(
                id = 1L,
                product =
                    Product(
                        id = 1L,
                        imageUrl =
                            """
                            https://s3-alpha-sig.figma.com/img/6a52/2b6a/05b81120d274b875b55d6da04de4749e?Expires=1716768000&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=bF~PKj~RVd3Rg-U5LFz7izxp92X8QuDAiYxB6S4iXS41nq6BXyKGCmyZdn39WHzQIeIscToVmlFB7WwRKhnfDR-WxMbd7dTuJIRhwiR8iQ5lq6LUm7MLNEv9l779WDsICq0kUJp1MUPVJFDG72HKGqMJaL5KuqlmJeOhlT2Qy1rneIXyjuILXnqAbS56t3YlIPIPTI6BWe3Sk6j4zCPL49M0NNbkTY4bESgBSkqIzfXHTngQyHKXXbn~gM7IQSIumumWVSxY8j3Ms2q813-NrE7J0D1EMRQokCmMQDTnaIzioYiDgIAnoBwurFdR6Ehl~VJfS55vWo50ajYaaGKPMQ__
                            """.trimIndent(),
                        price = 12_000,
                        name = "PET보틀-납작(2000ml) 밀크티",
                        category = "",
                    ),
            ),
            CartItem(
                id = 2L,
                product =
                    Product(
                        id = 2L,
                        imageUrl =
                            """
                            https://s3-alpha-sig.figma.com/img/6a52/2b6a/05b81120d274b875b55d6da04de4749e?Expires=1716768000&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=bF~PKj~RVd3Rg-U5LFz7izxp92X8QuDAiYxB6S4iXS41nq6BXyKGCmyZdn39WHzQIeIscToVmlFB7WwRKhnfDR-WxMbd7dTuJIRhwiR8iQ5lq6LUm7MLNEv9l779WDsICq0kUJp1MUPVJFDG72HKGqMJaL5KuqlmJeOhlT2Qy1rneIXyjuILXnqAbS56t3YlIPIPTI6BWe3Sk6j4zCPL49M0NNbkTY4bESgBSkqIzfXHTngQyHKXXbn~gM7IQSIumumWVSxY8j3Ms2q813-NrE7J0D1EMRQokCmMQDTnaIzioYiDgIAnoBwurFdR6Ehl~VJfS55vWo50ajYaaGKPMQ__
                            """.trimIndent(),
                        price = 12_000,
                        name = "PET보틀-밀크티(600ml)",
                        category = "",
                    ),
            ),
        )

    override suspend fun addCartItem(product: Product): Result<Unit> {
        val cartItem = CartItem(3L, product)
        cartItems.add(cartItem)
        return Result.success(Unit)
    }

    override suspend fun loadPagingCartItems(
        offset: Int,
        pagingSize: Int,
    ): Result<List<CartItem>> {
        return if (offset + pagingSize <= cartItems.size) {
            Result.success(cartItems.subList(offset, offset + pagingSize))
        } else {
            Result.success(cartItems.subList(offset, cartItems.size))
        }
    }

    override suspend fun deleteCartItem(itemId: Long): Result<Unit> {
        cartItems.removeIf { it.id == itemId }
        return Result.success(Unit)
    }

    override suspend fun getCartItemResultFromProductId(productId: Long): Result<CartItemResult> {
        return Result.success(CartItemResult(0, CartItemCounter()))
    }

    override suspend fun updateCartItem(
        product: Product,
        updateCartItemType: UpdateCartItemType,
    ): Result<UpdateCartItemResult> {
        return Result.success(UpdateCartItemResult.UPDATED(CartItemResult(0, CartItemCounter())))
    }

    override suspend fun getTotalCartItemCount(): Result<Int> {
        return Result.success(cartItems.size)
    }
}
