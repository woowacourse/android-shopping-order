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
import woowacourse.shopping.data.remote.dto.response.QuantityResponse
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Recent
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.Repository

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

    override fun getProducts(
        category: String,
        page: Int,
        size: Int,
        callback: (Result<List<CartProduct>?>) -> Unit
    ) {
        remoteDataSource.getProducts(category, page, size) { result ->
            result.onSuccess { callback(Result.success(it.content.map { it.toDomain() })) }
                .onFailure { callback(Result.failure(it)) }
        }
    }

    override fun getProductsByPaging(callback: (Result<List<CartProduct>?>) -> Unit) {
        productPagingSource.load { result ->
            when (result) {
                is LoadResult.Page -> {
                    callback(Result.success(result.data))
                }

                is LoadResult.Error -> {
                    callback(Result.failure(Throwable(result.message)))
                }
            }
        }
    }

    override fun getCartItems(
        page: Int,
        size: Int,
        callback: (Result<List<CartProduct>?>) -> Unit
    ) {
        remoteDataSource.getCartItems { result ->
            result.onSuccess { callback(Result.success(it.content.map { it.toDomain() })) }
                .onFailure { callback(Result.failure(it)) }
        }
    }

    override fun getProductById(id: Int): Result<CartProduct?> =
        runCatching {
            val response = remoteDataSource.getProductById(id = id)
            if (response.isSuccessful) {
                return Result.success(response.body()?.toDomain())
            }
            return Result.failure(Throwable())
        }

    override fun postCartItem(cartItemRequest: CartItemRequest): Result<Int> =
        runCatching {
            val response = remoteDataSource.postCartItem(cartItemRequest)
            if (response.isSuccessful) {
                return Result.success(
                    response.headers()["LOCATION"]?.substringAfterLast("/")?.toIntOrNull() ?: 0,
                )
            }
            return Result.failure(Throwable())
        }

    override fun patchCartItem(
        id: Int,
        quantityRequest: QuantityRequest,
    ): Result<Unit> =
        runCatching {
            val response = remoteDataSource.patchCartItem(id, quantityRequest)
            if (response.isSuccessful) {
                return Result.success(Unit)
            }
            return Result.failure(Throwable())
        }

    override fun deleteCartItem(id: Int): Result<Unit> =
        runCatching {
            val response = remoteDataSource.deleteCartItem(id)
            if (response.isSuccessful) {
                return Result.success(Unit)
            }
            return Result.failure(Throwable())
        }

    override fun postOrders(orderRequest: OrderRequest): Result<Unit> =
        runCatching {
            val response = remoteDataSource.postOrders(orderRequest)
            if (response.isSuccessful) {
                return Result.success(Unit)
            }
            return Result.failure(Throwable())
        }

    override fun findCartByPaging(
        offset: Int,
        pageSize: Int,
    ): Result<List<CartProduct>> =
        runCatching {
            localDataSource.findCartByPaging(offset, pageSize).map { it.toDomain() }
        }

    override fun findByLimit(limit: Int): Result<List<RecentProduct>> =
        runCatching {
            localDataSource.findByLimit(limit).map { it.toDomain() }
        }

    override fun findOne(): Result<RecentProduct?> =
        runCatching {
            localDataSource.findOne()?.toDomain()
        }

    override fun findProductById(id: Long): Result<CartProduct?> =
        runCatching {
            localDataSource.findProductById(id)?.toDomain()
        }

    override fun saveCart(cart: Cart): Result<Long> =
        runCatching {
            localDataSource.saveCart(cart.toEntity())
        }

    override fun saveRecent(recent: Recent): Result<Long> =
        runCatching {
            localDataSource.saveRecent(recent.toEntity())
        }

    override fun saveRecentProduct(recentProduct: RecentProduct): Result<Long> =
        runCatching {
            localDataSource.saveRecentProduct(recentProduct.toEntity())
        }

    override fun deleteCart(id: Long): Result<Long> =
        runCatching {
            localDataSource.deleteCart(id)
        }

    override fun getMaxCartCount(): Result<Int> =
        runCatching {
            localDataSource.getMaxCartCount()
        }


    override fun getCartItemsCounts(callback: (Result<QuantityResponse>) -> Unit) {
        remoteDataSource.getCartItemsCounts { result ->
            callback(result)
        }
    }

    override fun getCuration(callback: (Result<List<CartProduct>>) -> Unit) {
        localDataSource.findOne()?.toDomain()?.let {
            remoteDataSource.getProducts(it.category, 0, 10) { productResult ->
                productResult.onSuccess { products ->
                    remoteDataSource.getCartItems(0, 1000) { cartResult ->
                        cartResult.onSuccess { cartItems ->
                            val cartProductIds = cartItems.content.map { it.product.id }.toSet()

                            val filteredProducts =
                                products.content.filter { product -> product.id !in cartProductIds }

                            val cartProducts =
                                filteredProducts.map { product ->
                                    val cart =
                                        cartItems.content.find { it.product.id == product.id }

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
                            callback(Result.success(cartProducts))
                        }
                    }
                }
            }
        }
    }
}
