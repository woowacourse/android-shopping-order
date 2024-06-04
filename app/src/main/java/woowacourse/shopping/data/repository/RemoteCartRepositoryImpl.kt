package woowacourse.shopping.data.repository

import android.util.Log
import retrofit2.Response
import woowacourse.shopping.data.database.ProductClient
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.model.dto.CartItemDto
import woowacourse.shopping.data.model.dto.CartItemsDto
import woowacourse.shopping.data.model.dto.ContentDto
import woowacourse.shopping.data.model.dto.ShoppingProductDto
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ShoppingCart
import woowacourse.shopping.domain.repository.CartRepository
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class RemoteCartRepositoryImpl : CartRepository {
    private val service = ProductClient.service
    private var cartItemData: CartItemDto? = null
    private var cartItems: List<CartItem>? = null

    init {
        updateCartItems()
    }

    override fun updateCartItems() {
        var response: Response<CartItemDto>? = null
        threadAction {
            response = service.requestCartItems().execute()
            cartItemData = (response as Response<CartItemDto>).body()
            cartItems = cartItemData?.content?.map { it.toDomainModel() }
            Log.d("crong", "Repository cartItems: $cartItems")
        }
    }

    override fun insert(
        product: Product,
        quantity: Int,
    ) {
        threadAction {
            val cartId = findCartItemIdWithProductId(product.id)
            if (cartId == -1L) {
                service.addCartItem(ShoppingProductDto(product.id, quantity)).execute()
            } else {
                service.updateCartItemQuantity(cartId, quantity).execute()
            }
        }
        updateCartItems()
    }

    override fun update(
        productId: Long,
        quantity: Int,
    ) {
        threadAction {
            service.updateCartItemQuantity(productId, quantity).execute()
        }
        updateCartItems()
    }

    override fun updateQuantity(
        cartItemId: Long,
        quantity: Int,
    ) {
        threadAction {
            service.updateCartItemQuantity(cartItemId, quantity).execute()
        }
        updateCartItems()
    }

    override fun updateQuantityWithProductId(
        productId: Long,
        quantity: Int,
    ) {
        val contentId = findCartItemIdWithProductId(productId)
        updateQuantity(contentId, quantity)
    }

    private fun findCartItemIdWithProductId(productId: Long): Long {
        return cartItems?.find { it.productId == productId }?.id ?: -1L
    }

    override fun findQuantityWithProductId(productId: Long): Int {
        val contentId = findCartItemIdWithProductId(productId)
        return cartItems?.find { it.id == contentId }?.quantity ?: 0
    }

    override fun makeOrder(order: Order) {
        val cartItemIds = order.list.map { it.id }
        service.makeOrder(CartItemsDto(cartItemIds)).execute()
    }

    override fun size(): Int {
        return cartItemData?.totalElements ?: 0
    }

    override fun sumOfQuantity(): Int {
        var quantity: Int = 0
        var responseCode: Int = -1

        threadAction {
            val response = service.requestCartItemsCount().execute()
            responseCode = response.code()
            quantity = response.body()?.quantity ?: -1
        }
        while (true) {
            Thread.sleep(1000)
            if (quantity != -1 || responseCode != -1) {
                break
            }
        }
        // if (quantity != -1 || responseCode != -1) throw IllegalStateException("Failed to get quantity")

        return quantity
    }

    override fun findOrNullWithProductId(productId: Long): CartItem? {
        var contentDto: ContentDto? = null
        threadAction {
            contentDto =
                service.requestCartItems().execute().body()?.content?.find {
                    it.product.id == productId
                }
        }
        return contentDto?.toDomainModel()
    }

    override fun findWithCartItemId(cartItemId: Long): CartItem {
        return cartItems?.find { it.id == cartItemId } ?: throw NoSuchElementException()
    }

    override fun findAll(): ShoppingCart {
        return ShoppingCart(cartItems ?: emptyList())
    }

    override fun findAllPagedItems(
        page: Int,
        pageSize: Int,
    ): ShoppingCart {
        var cartItems: List<CartItem> = emptyList()
        var response: Int = -1
        threadAction {
            cartItems = service.requestCartItems(page, pageSize).execute().body()?.content?.map {
                it.toDomainModel()
            } ?: emptyList()
            response = service.requestCartItems(page, pageSize).execute().code()
        }

        if (cartItems.isEmpty() && response == -1) throw IllegalStateException("Failed to get cart items")

        return ShoppingCart(cartItems)
    }

    override fun delete(cartItemId: Long) {
        threadAction {
            service.deleteCartItem(cartItemId).execute()
        }
        updateCartItems()
    }

    override fun deleteWithProductId(productId: Long) {
        val contentId = findCartItemIdWithProductId(productId)
        threadAction {
            service.deleteCartItem(contentId).execute()
        }
        updateCartItems()
    }

    override fun deleteAll() {
        if (cartItems.isNullOrEmpty()) {
            return
        } else {
            cartItems!!.forEach {
                threadAction {
                    service.deleteCartItem(it.id).execute()
                }
            }
        }
        updateCartItems()
    }

    private fun threadAction(action: () -> Unit) {
        val latch = CountDownLatch(1)
        thread {
            action()
            latch.countDown()
        }
        latch.await()
    }
}
