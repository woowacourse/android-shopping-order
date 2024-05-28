package woowacourse.shopping.remote

import woowacourse.shopping.data.model.ProductData
import woowacourse.shopping.data.source.ProductDataSource

class ProductRemoteDataSource(private val productsApiService: ProductsApiService) : ProductDataSource {
    override fun findByPaged(page: Int): List<ProductData> {
        val response =
            productsApiService.requestProducts("fashion", page).execute().body()?.content
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
        return true
    }

    override fun shutDown(): Boolean {
        return false
    }
}
