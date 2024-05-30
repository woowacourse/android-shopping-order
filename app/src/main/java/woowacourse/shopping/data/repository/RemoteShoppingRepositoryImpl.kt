package woowacourse.shopping.data.repository

import woowacourse.shopping.data.database.ProductClient
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.model.dto.ProductResponseDto
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ShoppingItemsRepository
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class RemoteShoppingRepositoryImpl : ShoppingItemsRepository {
    private val service = ProductClient.service
    private var productData: ProductResponseDto? = null
    private var products: List<Product>? = null

    init {
        threadAction {
            productData = service.requestProducts().execute().body()
            products = productData?.content?.map { it.toDomainModel() }
        }
    }

    override fun fetchProductsSize(): Int {
        return productData?.totalElements ?: 0
    }

    override fun fetchProductsWithIndex(
        start: Int,
        end: Int,
    ): List<Product> {
        return products?.subList(start, end) ?: emptyList()
    }

    override fun findProductItem(id: Long): Product? {
        var product: Product? = null
        threadAction {
            product = service.requestProduct(id).execute().body()?.toDomainModel()
        }

        return product
    }

    private fun threadAction(action: () -> Unit) {
        val latch = CountDownLatch(1)
        thread {
            action()
            latch.countDown()
        }
        latch.await()
    }
}
