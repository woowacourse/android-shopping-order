package woowacourse.shopping.remote.source

import woowacourse.shopping.data.model.ProductData
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.remote.service.ProductsApiService

class ProductRemoteDataSource(private val productsApiService: ProductsApiService) : ProductDataSource {
    override fun findByPaged(page: Int): List<ProductData> {
        val response =
            productsApiService.requestProducts(page = page).execute().body()?.content
                ?: throw NoSuchElementException("there is no product with page: $page")
        return response.map {
            ProductData(
                id = it.id.toLong(),
                imgUrl = it.imageUrl,
                name = it.name,
                price = it.price,
            )
        }
    }

    override fun findById(id: Long): ProductData {
        val response =
            productsApiService.requestProduct(id.toInt()).execute().body()
                ?: throw NoSuchElementException("there is no product with id: $id")
        return ProductData(
            id = response.id.toLong(),
            imgUrl = response.imageUrl,
            name = response.name,
            price = response.price,
        )
    }

    override fun isFinalPage(page: Int): Boolean {
        val totalPage = productsApiService.requestProducts(page = page).execute().body()?.totalPages
        return (page + 1) == totalPage
    }

    override fun shutDown(): Boolean {
        // TODO: 연결 끊기
        return false
    }

    companion object {
        private const val TAG = "ProductRemoteDataSource"
    }
}
