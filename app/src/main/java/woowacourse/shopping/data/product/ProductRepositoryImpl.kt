package woowacourse.shopping.data.product

import woowacourse.shopping.data.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.dto.response.ResponseProductsGetDto
import woowacourse.shopping.data.service.ApiFactory
import woowacourse.shopping.model.Product
import kotlin.concurrent.thread

class ProductRepositoryImpl : ProductRepository {
    override fun getProducts(
        page: Int,
        size: Int,
    ): List<Product> {
        var productsDto: ResponseProductsGetDto? = null
        thread {
            productsDto = ApiFactory.getProductsByOffset(page, size)
        }.join()
        val products = productsDto ?: error("상품 정보를 불러오지 못했습니다")
        return products.content.map { product ->
            Product(
                id = product.id,
                imageUrl = product.imageUrl,
                name = product.name,
                price = product.price,
            )
        }
    }

    override fun find(id: Long): Product {
        var productDto: ResponseProductIdGetDto? = null
        thread {
            productDto = ApiFactory.getProductsById(id)
        }.join()
        val product = productDto ?: error("$id 에 해당하는 productId가 없습니다")
        return Product(
            id = product.id,
            imageUrl = product.imageUrl,
            name = product.name,
            price = product.price,
        )
    }
}
