package woowacourse.shopping.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import woowacourse.shopping.data.database.ProductClient
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.model.dto.CartItemDto
import woowacourse.shopping.data.model.dto.CartItemsDto
import woowacourse.shopping.data.model.dto.Content
import woowacourse.shopping.data.model.dto.ShoppingProductDto
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ShoppingCart
import woowacourse.shopping.domain.repository.CartRepository

class RemoteCartRepositoryImpl : CartRepository {
    private val service = ProductClient.service
    private var cartItemData: CartItemDto? = null
    private var cartItems: List<CartItem>? = null

    init {
        updateCartItems()
    }

    override fun updateCartItems() {
        var response: Response<CartItemDto>? = null
        CoroutineScope(Dispatchers.IO).launch {
            response = service.requestCartItems().execute()
            cartItemData = (response as Response<CartItemDto>).body()
            cartItems = cartItemData?.content?.map { it.toDomainModel() }
        }

        while (true) {
            Thread.sleep(1000)
            if (cartItemData != null || response?.code() != 200) {
                break
            }
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
        service.updateCartItemQuantity(cartItemId, quantity).execute()
        updateCartItems()
    }

    override fun updateQuantityWithProductId(
        productId: Long,
        quantity: Int,
    ) {
        val contentId = findCartItemIdWithProductId(productId)
        service.updateCartItemQuantity(contentId, quantity).execute()
        updateCartItems()
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
        var response: Int = -1

        threadAction {
            quantity = service.requestCartItemsCount().execute().body()?.quantity ?: -1
        }
        while (true) {
            Thread.sleep(1000)
            if (quantity != -1 || response != -1) {
                break
            }
        }

        return quantity
    }

    override fun findOrNullWithProductId(productId: Long): CartItem? {
        var content: Content? = null
        threadAction {
            content =
                service.requestCartItems().execute().body()?.content?.find {
                    it.product.id == productId
                }
        }
        return content?.toDomainModel()
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
        val offset = page * pageSize
        val limit = pageSize
        var cartItems: List<CartItem> = emptyList()
        var response: Int = -1
        threadAction {
            cartItems = service.requestCartItems(page, pageSize).execute().body()?.content?.map {
                it.toDomainModel()
            } ?: emptyList()
            response = service.requestCartItems(page, pageSize).execute().code()
        }
        while (true) {
            Thread.sleep(1000)
            if (cartItems.isNotEmpty() || response != -1) {
                break
            }
        }

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
        CoroutineScope(Dispatchers.IO).launch {
            action()
        }
    }
}
