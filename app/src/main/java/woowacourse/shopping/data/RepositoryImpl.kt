package woowacourse.shopping.data

import android.util.Log
import woowacourse.shopping.data.local.RecentProductDataSource
import woowacourse.shopping.data.local.mapper.toDomain
import woowacourse.shopping.data.local.mapper.toEntity
import woowacourse.shopping.data.remote.RemoteDataSource
import woowacourse.shopping.data.remote.dto.mapper.toDomain
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.data.remote.paging.LoadResult
import woowacourse.shopping.data.remote.paging.ProductPagingSource
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Coupon
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.utils.toIdOrNull

class RepositoryImpl(
    private val recentProductDataSource: RecentProductDataSource,
    private val remoteDataSource: RemoteDataSource,
) : Repository {
    private val productPagingSource = ProductPagingSource(remoteDataSource)

    override suspend fun getProducts(
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

    override suspend fun getProductsByPaging(
        offset: Int,
        pageSize: Int,
    ): Result<LoadResult.Page<CartProduct>> {
        return when (val data = productPagingSource.load(defaultOffset = offset, defaultPageSize = pageSize)) {
            is LoadResult.Page -> {
                Result.success(data)
            }
            is LoadResult.Error -> {
                Result.failure(Throwable(data.errorType.message))
            }
        }
    }

    override suspend fun getCartItems(
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

    override suspend fun getProductById(id: Int): Result<CartProduct?> =
        runCatching {
            val response = remoteDataSource.getProductById(id = id)
            if (response.isSuccessful) {
                return Result.success(response.body()?.toDomain())
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override suspend fun postCartItem(cartItemRequest: CartItemRequest): Result<Int> =
        runCatching {
            val response = remoteDataSource.postCartItem(cartItemRequest)
            if (response.isSuccessful) {
                return Result.success(
                    response.toIdOrNull() ?: 0,
                )
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override suspend fun patchCartItem(
        id: Int,
        quantityRequestDto: QuantityRequest,
    ): Result<Unit> =
        runCatching {
            Log.d("SDFEFS", "${id}")
            val response = remoteDataSource.patchCartItem(id, quantityRequestDto)
            if (response.isSuccessful) {
                return Result.success(Unit)
            }
            Log.d("SDFEFS", response.toString())
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override suspend fun deleteCartItem(id: Int): Result<Unit> =
        runCatching {
            val response = remoteDataSource.deleteCartItem(id)
            if (response.isSuccessful) {
                return Result.success(Unit)
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override suspend fun postOrders(orderRequest: OrderRequest): Result<Unit> =
        runCatching {
            val response = remoteDataSource.postOrders(orderRequest)
            if (response.isSuccessful) {
                return Result.success(Unit)
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override suspend fun findByLimit(limit: Int): Result<List<RecentProduct>> =
        runCatching {
            recentProductDataSource.findByLimit(limit).map { it.toDomain() }
        }

    override suspend fun findOneRecent(): Result<RecentProduct?> =
        runCatching {
            recentProductDataSource.findOne()?.toDomain()
        }

    override suspend fun saveRecentProduct(recentProduct: RecentProduct): Result<Long> =
        runCatching {
            recentProductDataSource.saveRecentProduct(recentProduct.toEntity())
        }

    override suspend fun getCartItemsCounts(): Result<Int> =
        runCatching {
            val response = remoteDataSource.getCartItemsCounts()
            if (response.isSuccessful) {
                return Result.success(response.body()?.quantity ?: 0)
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override suspend fun getCuration(): Result<List<CartProduct>> {
        recentProductDataSource.findOne()?.toDomain()?.let {
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

    override suspend fun getCoupons(): Result<List<Coupon>> = runCatching {
        val response = remoteDataSource.getCoupons()
        if (response.isSuccessful) {
            return Result.success(response.body()?.map { it.toDomain() } ?: emptyList())
        }
        return Result.failure(Throwable(response.errorBody().toString()))
    }.onFailure {
    }
}
