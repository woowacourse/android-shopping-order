package woowacourse.shopping.data.repository

import kotlinx.coroutines.coroutineScope
import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.datasource.impl.RemoteCartDataSource
import woowacourse.shopping.data.remote.api.resultOrNull
import woowacourse.shopping.data.remote.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.remote.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemsGetDto
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.result.Fail
import woowacourse.shopping.domain.result.Result
import woowacourse.shopping.domain.result.handleApiResult
import woowacourse.shopping.domain.result.resultOrThrow
import woowacourse.shopping.domain.result.resultOrNull

class CartRepositoryImpl(private val dataSource: CartDataSource = RemoteCartDataSource()) :
    CartRepository {
    override suspend fun cartItem(productId: Long): CartWithProduct {
        val carts = allCartItems()
        return carts.first { it.product.id == productId }
    }

    override suspend fun cartItemOrNull(productId: Long): CartWithProduct? {
        return when (val carts = allCartItemsResponse()) {
            is Result.Success -> carts.result.firstOrNull { it.product.id == productId }
            is Fail -> null
            is Result.Exception -> null
        }
    }

    override suspend fun cartItemResponse(productId: Long): Result<CartWithProduct> {
        return when (val carts = allCartItemsResponse()) {
            is Result.Success -> {
                val cartWithProduct = carts.result.firstOrNull { it.product.id == productId }
                if (cartWithProduct != null) {
                    Result.Success(cartWithProduct)
                } else {
                    Fail.NotFound("$productId 에 해당하는 cartItem이 없습니다.")
                }
            }

            is Fail.NotFound -> Fail.NotFound(carts.message)
            is Fail.InvalidAuthorized -> Fail.InvalidAuthorized(carts.message)
            is Fail.Network -> Fail.Network(carts.message)
            is Result.Exception -> Result.Exception(carts.e)
        }
    }

    override suspend fun allCartItems(): List<CartWithProduct> {
        val count = dataSource.getCartItemCounts().resultOrNull()?.quantity ?: DEFAULT_CART_COUNT
        val response =
            handleApiResult(
                result = dataSource.getCartItems(0, count),
                transform = ResponseCartItemsGetDto::toCartWithProduct,
            )
        return if (response is Fail.NotFound) emptyList() else response.resultOrThrow()
    }

    override suspend fun allCartItemsResponse(): Result<List<CartWithProduct>> =
        coroutineScope {
            val count =
                dataSource.getCartItemCounts().resultOrNull()?.quantity ?: DEFAULT_CART_COUNT
            return@coroutineScope handleApiResult(
                result = dataSource.getCartItems(START_CART_PAGE, count),
                transform = ResponseCartItemsGetDto::toCartWithProduct,
            )
        }

    override suspend fun postCartItems(
        productId: Long,
        quantity: Int,
    ): Result<Unit> =
        handleApiResult(
            result = dataSource.postCartItems(RequestCartItemPostDto(productId, quantity)),
        )

    override suspend fun deleteCartItem(id: Long): Result<Unit> = handleApiResult(
        result = dataSource.deleteCartItems(id)
    )

    override suspend fun cartItemsCount(): Int =
        handleApiResult(
            result = dataSource.getCartItemCounts(),
            transform = { it.quantity },
        ).resultOrThrow()

    override suspend fun cartItemsCountOrNull(): Int? =
        handleApiResult(
            result = dataSource.getCartItemCounts(),
            transform = { it.quantity },
        ).resultOrNull()

    override suspend fun cartItemsCountResponse(): Result<Int> =
        handleApiResult(
            result = dataSource.getCartItemCounts(),
            transform = { it.quantity },
        )

    override suspend fun patchCartItem(
        id: Long,
        quantity: Int,
    ): Result<Unit> =
        handleApiResult(
            result =
                dataSource.patchCartItems(
                    id = id,
                    request = RequestCartItemsPatchDto(quantity),
                ),
        )

    override suspend fun addProductToCart(
        productId: Long,
        quantity: Int,
    ): Result<Unit> =
        coroutineScope {
            val cart: CartWithProduct? = allCartItems().firstOrNull { it.product.id == productId }
            if (cart == null) {
                return@coroutineScope postCartItems(productId, quantity)
            } else {
                return@coroutineScope patchCartItem(cart.id, cart.quantity.value + quantity)
            }
        }

    companion object {
        private const val DEFAULT_CART_COUNT = 300
        private const val START_CART_PAGE = 0
    }
}
