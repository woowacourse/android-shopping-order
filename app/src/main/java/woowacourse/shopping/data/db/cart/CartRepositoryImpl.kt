package woowacourse.shopping.data.db.cart

import woowacourse.shopping.data.model.CartItemRequestBody
import woowacourse.shopping.data.model.CartQuantity
import woowacourse.shopping.data.model.CartResponse
import woowacourse.shopping.data.remote.RemoteCartDataSource
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.toCartItemEntity
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CartRepository2
import kotlin.concurrent.thread

class CartRepositoryImpl(database: CartDatabase) : CartRepository {
    private val dao = database.cartDao()

    override fun save(
        product: Product,
        quantity: Int,
    ) {
        if (findOrNullByProductId(product.id) != null) {
            update(product.id, quantity)
        } else {
            threadAction {
                dao.save(product.toCartItemEntity(quantity))
            }
        }
    }

    override fun update(
        productId: Int,
        quantity: Int,
    ) {
        threadAction {
            dao.update(productId, quantity)
        }
    }

    override fun cartItemSize(): Int {
        var itemSize = 0
        threadAction {
            itemSize = dao.cartItemSize()
        }
        return itemSize
    }

    override fun productQuantity(productId: Int): Int {
        var productQuantity = 0
        threadAction {
            productQuantity = dao.productQuantity(productId)
        }
        return productQuantity
    }

    override fun findOrNullByProductId(productId: Int): CartItem? {
        var cartItemEntity: CartItemEntity? = null
        threadAction {
            cartItemEntity = dao.findByProductId(productId)
        }

        return cartItemEntity?.toCartItem()
    }

    override fun find(cartItemId: Int): CartItem {
        var cartItemEntity: CartItemEntity? = null
        threadAction {
            cartItemEntity = dao.find(cartItemId)
        }

        return cartItemEntity?.toCartItem() ?: throw IllegalArgumentException("데이터가 존재하지 않습니다.")
    }

    override fun findAll(): List<CartItem> {
        var cartItems: List<CartItem> = emptyList()
        threadAction {
            cartItems = dao.findAll().map { it.toCartItem() }
        }
        return cartItems
    }

    override fun findAllPagedItems(
        page: Int,
        pageSize: Int,
    ): List<CartItem> {
        var cartItems: List<CartItem> = emptyList()
        val offset = page * pageSize

        threadAction {
            cartItems =
                dao.findAllPaged(offset = offset, limit = pageSize)
                    .map { it.toCartItem() }
        }

        return cartItems
    }

    override fun delete(cartItemId: Int) {
        threadAction {
            dao.delete(cartItemId)
        }
    }

    override fun deleteAll() {
        threadAction {
            dao.deleteAll()
        }
    }

    private fun threadAction(action: () -> Unit) {
        val thread = Thread(action)
        thread.start()
        thread.join()
    }
}

class CartRepositoryImpl2(
    private val remoteCartDataSource: RemoteCartDataSource,
) : CartRepository2 {
    override fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): Result<CartResponse> {
        var result: Result<CartResponse>? = null
        thread {
            result =
                runCatching {
                    val response =
                        remoteCartDataSource.getCartItems(page, size, sort)
                            .execute()
                    if (response.isSuccessful) {
                        response.body() ?: throw Exception("No data available")
                    } else {
                        throw Exception("Error fetching data")
                    }
                }
        }.join()
        return result ?: throw Exception()
    }

    override fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Result<Unit> {
        var result: Result<Unit>? = null
        thread {
            result =
                runCatching {
                    val cartItemRequestBody = CartItemRequestBody(productId, quantity)
                    val request =
                        remoteCartDataSource.addCartItem(cartItemRequestBody)
                    val response = request.execute()
                    if (response.isSuccessful) {
                        response.body() ?: throw Exception("No data available")
                    } else {
                        throw Exception("Error fetching data")
                    }
                }
        }.join()

        return result ?: throw Exception()
    }

    override fun deleteCartItem(cartItemId: Int): Result<Unit> {
        var result: Result<Unit>? = null
        thread {
            result =
                runCatching {
                    val response =
                        remoteCartDataSource.deleteCartItem(cartItemId).execute()
                    if (response.isSuccessful) {
                        response.body() ?: throw Exception("No data available")
                    } else {
                        throw Exception("Error fetching data")
                    }
                }
        }.join()

        return result ?: throw Exception()
    }

    override fun updateCartItem(
        cartItemId: Int,
        quantity: Int,
    ): Result<Unit> {
        var result: Result<Unit>? = null
        thread {
            result =
                runCatching {
                    val cartQuantity = CartQuantity(quantity)
                    val response =
                        remoteCartDataSource.updateCartItem(cartItemId, cartQuantity)
                            .execute()
                    if (response.isSuccessful) {
                        response.body() ?: throw Exception("No data available")
                    } else {
                        println(response.code())
                        println(response.errorBody())
                        throw Exception("Error fetching data")
                    }
                }
        }.join()

        return result ?: throw Exception()
    }

    override fun getCartTotalQuantity(): Result<CartQuantity> {
        var result: Result<CartQuantity>? = null
        thread {
            result =
                runCatching {
                    val response =
                        remoteCartDataSource.getCartTotalQuantity().execute()
                    if (response.isSuccessful) {
                        response.body() ?: throw Exception("No data available")
                    } else {
                        throw Exception("Error fetching data")
                    }
                }
        }.join()

        return result ?: throw Exception()
    }
}
