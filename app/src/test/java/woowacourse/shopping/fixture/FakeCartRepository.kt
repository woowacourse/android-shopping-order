package woowacourse.shopping.fixture

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository

class FakeCartRepository : CartRepository {
    override suspend fun fetchTotalCount(): Result<Int> = Result.success(3)

    override suspend fun fetchPagedCartItems(
        page: Int,
        pageSize: Int?,
    ): Result<List<CartItem>> = Result.success(ProductsFixture.dummyCartItems)

    override fun fetchCartItemById(productId: Long): CartItem? = ProductsFixture.dummyCartItem

    override suspend fun insertOrUpdate(
        product: Product,
        productQuantity: Int,
    ): Result<Unit> = Result.success(Unit)

    override suspend fun insertProduct(
        product: Product,
        productQuantity: Int,
    ): Result<Unit> = Result.success(Unit)

    override suspend fun updateProduct(
        cartId: Long,
        product: Product,
        quantity: Int,
    ): Result<Unit> = Result.success(Unit)

    override suspend fun increaseQuantity(productId: Long): Result<Unit> = Result.success(Unit)

    override suspend fun decreaseQuantity(productId: Long): Result<Unit> = Result.success(Unit)

    override suspend fun deleteProduct(productId: Long): Result<Unit> = Result.success(Unit)

    override suspend fun fetchAllCartItems(): Result<Unit> = Result.success(Unit)
}
