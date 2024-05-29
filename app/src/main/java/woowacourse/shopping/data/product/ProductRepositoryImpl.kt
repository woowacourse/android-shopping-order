package woowacourse.shopping.data.product

import woowacourse.shopping.data.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.service.ApiFactory
import woowacourse.shopping.model.Product
import kotlin.concurrent.thread

class ProductRepositoryImpl : ProductRepository {
    override fun getProducts(
        page: Int,
        size: Int,
        success: (List<Product>) -> Unit,
    ) {
        ApiFactory.getProductsByOffset(page, size, success)
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
