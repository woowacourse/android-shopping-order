package woowacourse.shopping.data.remote.server.repository

import woowacourse.shopping.data.remote.server.dto.cart.items.PatchQuantityRequest
import woowacourse.shopping.data.remote.server.dto.cart.items.PostCartRequest
import woowacourse.shopping.data.remote.server.dto.cart.items.toDomainPurchaseProduct
import woowacourse.shopping.data.remote.server.service.CartService
import woowacourse.shopping.domain.model.cart.CartPage
import woowacourse.shopping.domain.model.order.PurchaseProduct
import woowacourse.shopping.domain.model.order.PurchaseProducts
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(private val cartService: CartService) : CartRepository {

    override suspend fun insert(purchaseProduct: PurchaseProduct) {
        cartService.postCartItems(
            PostCartRequest(
                productId = purchaseProduct.id,
                quantity = purchaseProduct.count
            )
        )
    }

    override suspend fun updateCount(cartItemId: Long, newQuantity: Int) {
        cartService.patchQuantity(
            cartItemId = cartItemId,
            request = PatchQuantityRequest(newQuantity)
        )
    }

    override suspend fun deleteCartItem(purchaseProductId: Long) {
        cartService.deleteProduct(
            productId = purchaseProductId
        )
    }

    override suspend fun getProductCount(): Int = cartService.requestQuantity().quantity

    override suspend fun getCartPage(
        page: Int,
        size: Int
    ): CartPage {
        try {
            val response = cartService.requestCartItems(page, size)
            val cartItems = response.content.map { content->
                content.toDomainPurchaseProduct()
            }
            return CartPage(
                items = PurchaseProducts(cartItems),
                isLast = response.last || response.content.size < size,
            )
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllCartItems(pageSize: Int): PurchaseProducts {
        require(pageSize > 0) { "페이지 크기는 1 이상이어야 합니다." }

        val allItems = mutableListOf<PurchaseProduct>()
        var page = 0

        while (true) {
            val cartPage = getCartPage(page, pageSize)
            allItems += cartPage.items.purchaseProducts

            if (cartPage.isLast) break
            page++
        }

        return PurchaseProducts(allItems)
    }

    override suspend fun findCartItemByProductId(
        productId: Long,
        pageSize: Int,
    ): PurchaseProduct? {
        require(pageSize > 0) { "페이지 크기는 1 이상이어야 합니다." }

        var page = 0

        while (true) {
            val cartPage = getCartPage(page, pageSize)
            val foundItem = cartPage.items.findByProductId(productId)

            if (foundItem != null) return foundItem
            if (cartPage.isLast) return null

            page++
        }
    }
}
