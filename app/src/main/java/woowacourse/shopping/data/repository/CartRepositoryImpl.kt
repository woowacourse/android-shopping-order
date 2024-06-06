package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.datasource.impl.RemoteCartDataSource
import woowacourse.shopping.data.local.room.cart.Cart
import woowacourse.shopping.data.remote.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.remote.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemsGetDto
import woowacourse.shopping.domain.model.CartWithProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Quantity
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(private val dataSource: CartDataSource = RemoteCartDataSource()) :
    CartRepository {
    override suspend fun getCartItem(productId: Long): CartWithProduct {
        return getAllCartItemsWithProduct().firstOrNull { it.product.id == productId } ?: error(
            "장바구니 정보를 불러올 수 없습니다.",
        )
    }

    override suspend fun getAllCartItems(): List<Cart> {

        val response = dataSource.getCartItems(0, dataSource.getCartItemCounts().quantity)

        return response.content.map { it.toCart() }
    }

    override suspend fun getAllCartItemsWithProduct(): List<CartWithProduct> {
        val response = dataSource.getCartItems(0, dataSource.getCartItemCounts().quantity)

        return response.content.map {
            CartWithProduct(
                it.id,
                it.product.toProduct(),
                Quantity(it.quantity),
            )
        }
    }

    override suspend fun postCartItems(
        productId: Long,
        quantity: Int,
    ) {
        dataSource.postCartItems(RequestCartItemPostDto(productId, quantity))
    }

    override suspend fun deleteCartItem(id: Long) {
        dataSource.deleteCartItems(id)
    }

    override suspend fun getCartItemCounts(): Int = dataSource.getCartItemCounts().quantity


    override suspend fun patchCartItem(
        id: Long,
        quantity: Int,
    ) {
        dataSource.patchCartItems(id = id, request = RequestCartItemsPatchDto(quantity))
    }

    override suspend fun addProductToCart(
        productId: Long,
        quantity: Int,
    ) {
        val cart: Cart? = getAllCartItems().firstOrNull { it.productId == productId }
        if (cart == null) {
            postCartItems(productId, quantity)
            return
        }
        patchCartItem(cart.id, cart.quantity.value + quantity)
    }

}
