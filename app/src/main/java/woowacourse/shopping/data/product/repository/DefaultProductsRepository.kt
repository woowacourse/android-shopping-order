package woowacourse.shopping.data.product.repository

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.product.local.dao.RecentWatchingDao
import woowacourse.shopping.data.product.local.entity.RecentWatchingEntity
import woowacourse.shopping.data.product.remote.dto.ProductsResponseDto
import woowacourse.shopping.data.product.remote.service.ProductService
import woowacourse.shopping.domain.product.Product
import kotlin.concurrent.thread

class DefaultProductsRepository(
    private val productService: ProductService,
    private val recentWatchingDao: RecentWatchingDao,
) : ProductsRepository {
    override fun load(
        page: Int,
        size: Int,
        onResult: (Result<List<Product>>) -> Unit,
    ) {
        productService
            .getProducts(page = page, size = size)
            .enqueue(
                object : retrofit2.Callback<ProductsResponseDto> {
                    override fun onResponse(
                        call: Call<ProductsResponseDto>,
                        response: Response<ProductsResponseDto>,
                    ) {
                        onResult(Result.success(response.body()?.toDomain() ?: emptyList()))
                    }

                    override fun onFailure(
                        call: Call<ProductsResponseDto>,
                        t: Throwable,
                    ) {
                        onResult(Result.failure(t))
                    }
                },
            )
    }

    override fun getRecentWatchingProducts(
        size: Int,
        onResult: (Result<List<Product>>) -> Unit,
    ) {
        thread {
            runCatching {
                recentWatchingDao.getRecentWatchingProducts(size)
            }.onSuccess { recentWatchingProducts: List<RecentWatchingEntity> ->
                onResult(Result.success(recentWatchingProducts.map { it.product }))
            }.onFailure { exception ->
                onResult(Result.failure(exception))
            }
        }
    }

    override fun updateRecentWatchingProduct(
        product: Product,
        onResult: (Result<Unit>) -> Unit,
    ) {
        thread {
            runCatching {
                recentWatchingDao.insertRecentWatching(
                    RecentWatchingEntity(
                        productId = product.id,
                        product = product,
                    ),
                )
            }.onSuccess {
                onResult(Result.success(Unit))
            }.onFailure { exception ->
                onResult(Result.failure(exception))
            }
        }
    }

    companion object {
        @Suppress("ktlint:standard:property-naming")
        private var INSTANCE: ProductsRepository? = null

        fun initialize(
            recentWatchingDao: RecentWatchingDao,
            productService: ProductService,
        ) {
            if (INSTANCE == null) {
                INSTANCE =
                    DefaultProductsRepository(
                        recentWatchingDao = recentWatchingDao,
                        productService = productService,
                    )
            }
        }

        fun get(): ProductsRepository = INSTANCE ?: throw IllegalStateException("초기화 되지 않았습니다.")
    }
}
