package woowacourse.shopping.data

import woowacourse.shopping.data.local.LocalDataSource
import woowacourse.shopping.data.local.mapper.toDomain
import woowacourse.shopping.data.local.mapper.toEntity
import woowacourse.shopping.data.remote.paging.LoadResult
import woowacourse.shopping.data.remote.paging.ProductPagingSource
import woowacourse.shopping.data.remote.RemoteDataSource
import woowacourse.shopping.data.remote.dto.mapper.toDomain
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.Repository

class RepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
) : Repository {
    private val productPagingSource = ProductPagingSource(remoteDataSource)

    override fun getProducts(
        category: String,
        page: Int,
        size: Int,
    ): Result<List<CartProduct>> =
        runCatching {
            val response = remoteDataSource.getProducts(category, page, size)
            if (response.isSuccessful) {
                return Result.success(response.body()?.content?.map { it.toDomain() } ?: emptyList())
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override fun getProductsByPaging(): Result<List<CartProduct>> {
        return when (val data = productPagingSource.load()) {
            is LoadResult.Page -> {
                Result.success(data.data)
            }
            is LoadResult.Error -> {
                Result.failure(Throwable(data.errorType.message))
            }
        }
    }

    override fun getCartItems(
        page: Int,
        size: Int,
    ): Result<List<CartProduct>> =
        runCatching {
            val response = remoteDataSource.getCartItems(page, size)
            if (response.isSuccessful) {
                return Result.success(response.body()?.content?.map { it.toDomain() } ?: emptyList())
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override fun getProductById(id: Int): Result<CartProduct?> =
        runCatching {
            val response = remoteDataSource.getProductById(id = id)
            if (response.isSuccessful) {
                return Result.success(response.body()?.toDomain())
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override fun postCartItem(cartItemRequest: CartItemRequest): Result<Int> =
        runCatching {
            val response = remoteDataSource.postCartItem(cartItemRequest)
            if (response.isSuccessful) {
                return Result.success(
                    response.headers()["LOCATION"]?.substringAfterLast("/")?.toIntOrNull() ?: 0,
                )
            }
            return Result.failure(Throwable(response.errorBody().toString()))
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
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override fun deleteCartItem(id: Int): Result<Unit> =
        runCatching {
            val response = remoteDataSource.deleteCartItem(id)
            if (response.isSuccessful) {
                return Result.success(Unit)
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override fun postOrders(orderRequest: OrderRequest): Result<Unit> =
        runCatching {
            val response = remoteDataSource.postOrders(orderRequest)
            if (response.isSuccessful) {
                return Result.success(Unit)
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }
    override fun findByLimit(limit: Int): Result<List<RecentProduct>> =
        runCatching {
            localDataSource.findByLimit(limit).map { it.toDomain() }
        }

    override fun findOne(): Result<RecentProduct?> =
        runCatching {
            localDataSource.findOne()?.toDomain()
        }

    override fun saveRecentProduct(recentProduct: RecentProduct): Result<Long> =
        runCatching {
            localDataSource.saveRecentProduct(recentProduct.toEntity())
        }
    override fun getCartItemsCounts(): Result<Int> =
        runCatching {
            val response = remoteDataSource.getCartItemsCounts()
            if (response.isSuccessful) {
                return Result.success(response.body()?.quantity ?: 0)
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override fun getCuration(): Result<List<CartProduct>> {
        localDataSource.findOne()?.toDomain()?.let {
            val productResponse = remoteDataSource.getProducts(it.category, 0, 10)
            val cartResponse = remoteDataSource.getCartItems(0, 1000)

            if (productResponse.isSuccessful && cartResponse.isSuccessful) {
                val products = productResponse.body()?.content ?: emptyList()
                val cartItems = cartResponse.body()?.content ?: emptyList()

                val cartProductIds = cartItems.map { it.product.id }.toSet()

                val filteredProducts = products.filter { product -> product.id !in cartProductIds }

                val cartProducts =
                    filteredProducts.map { product ->
                        val cart = cartItems.find { it.product.id == product.id }

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
                return Result.success(cartProducts)
            } else {
                return Result.failure(Throwable())
            }
        }
        return Result.failure(Throwable())
    }
}
