package woowacourse.shopping.remote.source

import woowacourse.shopping.data.model.ProductData
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.remote.service.ProductsApiService

class ProductRemoteDataSource(private val productsApiService: ProductsApiService) : ProductDataSource {
    override suspend fun findByPaged2(page: Int): Result<List<ProductData>> =
        runCatching {
            productsApiService.requestProducts2(page = page).content.map {
                ProductData(
                    id = it.id,
                    imgUrl = it.imageUrl,
                    name = it.name,
                    price = it.price,
                )
            }
        }

    override suspend fun findAllUntilPage2(page: Int): Result<List<ProductData>> =
        runCatching {
            productsApiService.requestProducts2(size = page * 20).content.map {
                ProductData(
                    id = it.id,
                    imgUrl = it.imageUrl,
                    name = it.name,
                    price = it.price,
                )
            }
        }

    override suspend fun findById2(id: Long): Result<ProductData> =
        runCatching {
            productsApiService.requestProduct2(id.toInt()).let {
                ProductData(
                    id = it.id,
                    imgUrl = it.imageUrl,
                    name = it.name,
                    price = it.price,
                    category = it.category,
                )
            }
        }

    override suspend fun findByCategory2(category: String): Result<List<ProductData>> =
        runCatching {
            productsApiService.requestProducts2(category = category).content.map {
                ProductData(
                    id = it.id,
                    imgUrl = it.imageUrl,
                    name = it.name,
                    price = it.price,
                    category = it.category,
                )
            }
        }

    override suspend fun isFinalPage2(page: Int): Result<Boolean> =
        runCatching {
            val totalPage = productsApiService.requestProducts2(page = page).totalPages
            (page + 1) == totalPage
        }

    override fun shutDown(): Boolean {
        // TODO: 연결 끊기
        return false
    }

    companion object {
        private const val TAG = "ProductRemoteDataSource"
    }
}
