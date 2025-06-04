package woowacourse.shopping.fixture

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartRepository

class FakeCartRepository : CartRepository {
    override fun getTotalCount(onResult: (Result<Int>) -> Unit) {
        onResult(Result.success(3))
    }

    override fun fetchPagedCartItems(
        page: Int,
        pageSize: Int?,
        onResult: (Result<List<CartItem>>) -> Unit,
    ) {
        onResult(Result.success(ProductsFixture.dummyCartItems))
    }

    override fun getCartItemById(productId: Long): CartItem = ProductsFixture.dummyCartItem

    override fun insertOrUpdate(
        product: Product,
        productQuantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        onResult(Result.success(Unit))
    }

    override fun insertProduct(
        product: Product,
        productQuantity: Int,
        onResult: (Result<Long>) -> Unit,
    ) {
        onResult(Result.success(1))
    }

    override fun updateProduct(
        cartId: Long,
        product: Product,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        onResult(Result.success(Unit))
    }

    override fun increaseQuantity(
        productId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        onResult(Result.success(Unit))
    }

    override fun decreaseQuantity(
        productId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        onResult(Result.success(Unit))
    }

    override fun deleteProduct(
        productId: Long,
        onResult: (Result<Unit>) -> Unit,
    ) {
        onResult(Result.success(Unit))
    }
}
