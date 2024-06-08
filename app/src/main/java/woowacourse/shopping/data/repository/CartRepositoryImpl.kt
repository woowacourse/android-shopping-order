package woowacourse.shopping.data.repository

import kotlinx.coroutines.coroutineScope
import woowacourse.shopping.data.datasource.ApiHandleCartDataSource
import woowacourse.shopping.data.datasource.impl.ApiHandleCartDataSourceImpl
import woowacourse.shopping.data.remote.api.resultOrNull
import woowacourse.shopping.data.remote.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.remote.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemsGetDto
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.response.Fail
import woowacourse.shopping.domain.response.Response
import woowacourse.shopping.domain.response.handleApiResult
import woowacourse.shopping.domain.response.result
import woowacourse.shopping.domain.response.resultOrNull

class CartRepositoryImpl(private val dataSource: ApiHandleCartDataSource = ApiHandleCartDataSourceImpl()) :
    CartRepository {

    override suspend fun cartItem(productId: Long): CartWithProduct {
        val carts = allCartItems()
        return carts.first { it.product.id == productId }
    }

    override suspend fun cartItemOrNull(productId: Long): CartWithProduct? {
        return when (val carts = allCartItemsResponse()) {
            is Response.Success -> carts.result.firstOrNull { it.product.id == productId }
            is Fail -> null
            is Response.Exception -> null
        }
    }

    override suspend fun cartItemResponse(productId: Long): Response<CartWithProduct> {
        return when (val carts = allCartItemsResponse()) {
            is Response.Success -> {
                val cartWithProduct = carts.result.firstOrNull { it.product.id == productId }
                if (cartWithProduct != null) {
                    Response.Success(cartWithProduct)
                } else {
                    Response.Exception(NoSuchElementException())
                }
            }

            is Fail.NotFound -> Fail.NotFound(carts.message)
            is Fail.InvalidAuthorized -> Fail.InvalidAuthorized(carts.message)
            is Fail.Network -> Fail.Network(carts.message)
            is Response.Exception -> Response.Exception(carts.e)
        }
    }

    override suspend fun allCartItems(): List<CartWithProduct> {
        val count = dataSource.getCartItemCounts().resultOrNull()?.quantity ?: DEFAULT_CART_COUNT
        val response = handleApiResult(
            result = dataSource.getCartItems(0, count),
            transform = ResponseCartItemsGetDto::toCartWithProduct
        )
        return if (response is Fail.NotFound) emptyList() else response.result()
    }

    override suspend fun allCartItemsResponse(): Response<List<CartWithProduct>> = coroutineScope {
        val count = dataSource.getCartItemCounts().resultOrNull()?.quantity ?: DEFAULT_CART_COUNT
        return@coroutineScope handleApiResult(
            result = dataSource.getCartItems(START_CART_PAGE, count),
            transform = ResponseCartItemsGetDto::toCartWithProduct
        )
    }

    override suspend fun postCartItems(
        productId: Long,
        quantity: Int,
    ): Response<Unit> = handleApiResult(
        result = dataSource.postCartItems(RequestCartItemPostDto(productId, quantity)),
    )


    override suspend fun deleteCartItem(id: Long): Response<Unit> = handleApiResult(
        result = dataSource.deleteCartItems(id)
    )

    override suspend fun cartItemsCount(): Int = handleApiResult(
        result = dataSource.getCartItemCounts(),
        transform = { it.quantity }
    ).result()

    override suspend fun cartItemsCountOrNull(): Int? = handleApiResult(
        result = dataSource.getCartItemCounts(),
        transform = { it.quantity }
    ).resultOrNull()

    override suspend fun cartItemsCountResponse(): Response<Int> = handleApiResult(
        result = dataSource.getCartItemCounts(),
        transform = { it.quantity }
    )


    override suspend fun patchCartItem(
        id: Long,
        quantity: Int,
    ): Response<Unit> = handleApiResult(
        result = dataSource.patchCartItems(id = id, request = RequestCartItemsPatchDto(quantity))
    )

    override suspend fun addProductToCart(
        productId: Long,
        quantity: Int,
    ): Response<Unit> = coroutineScope {
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
