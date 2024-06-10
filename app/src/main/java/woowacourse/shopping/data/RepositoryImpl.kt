package woowacourse.shopping.data

import woowacourse.shopping.data.local.LocalDataSource
import woowacourse.shopping.data.local.mapper.toDomain
import woowacourse.shopping.data.local.mapper.toEntity
import woowacourse.shopping.data.remote.LoadResult
import woowacourse.shopping.data.remote.ProductPagingSource
import woowacourse.shopping.data.remote.RemoteDataSource
import woowacourse.shopping.data.remote.dto.mapper.toDomain
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Recent
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.domain.coupon.Coupon

class RepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
) : Repository {
    val productPagingSource = ProductPagingSource(remoteDataSource)

    override fun findProductByPaging(
        offset: Int,
        pageSize: Int,
    ): Result<List<CartProduct>> =
        runCatching {
            localDataSource.findProductByPaging(offset, pageSize).map { it.toDomain() }
        }

    override suspend fun getProducts(
        category: String,
        page: Int,
        size: Int,
    ): Result<List<CartProduct>?> {
        return remoteDataSource.getProducts(category, page, size)
            .mapCatching {
                it.map { it.toDomain() }
            }
            .recoverCatching {
                throw it
            }
    }

    override suspend fun getProductsByPaging(): Result<List<CartProduct>?> {
        val data = productPagingSource.load()
        return when (data) {
            is LoadResult.Page -> {
                Result.success(data.data)
            }

            is LoadResult.Error -> {
                Result.failure(Throwable(data.message))
            }
        }
    }

    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): Result<List<CartProduct>?> {
        return remoteDataSource.getCartItems(page, size)
            .mapCatching {
                it.map { it.toDomain() }
            }
            .recoverCatching {
                throw it
            }
    }

    override suspend fun postCartItem(cartItemRequest: CartItemRequest): Result<Int> {
        return remoteDataSource.addCartItem(cartItemRequest)
            .mapCatching { it.headers()["LOCATION"]?.substringAfterLast("/")?.toIntOrNull() ?: 0 }
            .recoverCatching { throw it }
    }

    override suspend fun updateCartItem(
        id: Int,
        quantityRequest: QuantityRequest,
    ): Result<Unit> {
        return remoteDataSource.updateCartItem(id, quantityRequest).recoverCatching { throw it }
    }

    override suspend fun deleteCartItem(id: Int): Result<Unit> {
        return remoteDataSource.deleteCartItem(id).recoverCatching { throw it }
    }

    override suspend fun submitOrders(orderRequest: OrderRequest): Result<Unit> {
        return remoteDataSource.submitOrders(orderRequest).recoverCatching { throw it }
    }

    override fun findCartByPaging(
        offset: Int,
        pageSize: Int,
    ): Result<List<CartProduct>> =
        runCatching {
            localDataSource.findCartByPaging(offset, pageSize).map { it.toDomain() }
        }

    override suspend fun findByLimit(limit: Int): Result<List<RecentProduct>> =
        runCatching {
            localDataSource.findByLimit(limit).map { it.toDomain() }
        }

    override suspend fun findOne(): Result<RecentProduct?> =
        runCatching {
            localDataSource.findOne()?.toDomain()
        }

    override fun saveCart(cart: Cart): Result<Long> =
        runCatching {
            localDataSource.saveCart(cart.toEntity())
        }

    override fun saveRecent(recent: Recent): Result<Long> =
        runCatching {
            localDataSource.saveRecent(recent.toEntity())
        }

    override suspend fun saveRecentProduct(recentProduct: RecentProduct): Result<Long> =
        runCatching {
            localDataSource.saveRecentProduct(recentProduct.toEntity())
        }

    override suspend fun updateRecentProduct(
        productId: Long,
        quantity: Int,
        cartId: Long,
    ) {
        localDataSource.updateRecentProduct(productId, quantity, cartId)
    }

    override fun deleteCart(id: Long): Result<Long> =
        runCatching {
            localDataSource.deleteCart(id)
        }

    override fun getMaxCartCount(): Result<Int> =
        runCatching {
            localDataSource.getMaxCartCount()
        }

    override suspend fun getCartItemsCounts(): Result<Int> {
        return remoteDataSource.getCartItemsCounts()
            .mapCatching { it.quantity }
            .recoverCatching { throw it }
    }

    override suspend fun getCuration(): Result<List<CartProduct>?> =
        runCatching {
            localDataSource.findOne()?.toDomain()?.let {
                val productResponse = remoteDataSource.getProducts(it.category, 0, 10)
                val cartResponse = remoteDataSource.getCartItems(0, 1000)

                if (productResponse.isSuccess && cartResponse.isSuccess) {
                    val products = productResponse.getOrNull()
                    val cartItems = cartResponse.getOrNull()

                    val cartProductIds = cartItems?.map { it.product.id }?.toSet()
                    val filteredProducts =
                        products?.filter { product -> product.id !in cartProductIds!! }
                    val cartProducts =
                        filteredProducts?.map { product ->
                            val cart = cartItems?.find { it.product.id == product.id }

                            CartProduct(
                                productId = product.id.toLong(),
                                name = product.name,
                                imgUrl = product.imageUrl,
                                price = product.price.toLong(),
                                category = product.category,
                                cartId = cart?.id?.toLong() ?: 0,
                                quantity = cart?.quantity ?: 0,
                            )
                        }
                    cartProducts
                } else {
                    throw Throwable()
                }
            } ?: throw Throwable()
        }

    override suspend fun getCoupons(): Result<List<Coupon>> {
        return remoteDataSource.getCoupons()
            .mapCatching { it.map { it.toDomain() } }
            .recoverCatching { throw it }
    }
}
