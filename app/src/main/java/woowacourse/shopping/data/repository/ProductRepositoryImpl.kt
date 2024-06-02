package woowacourse.shopping.data.repository

import woowacourse.shopping.data.db.product.MockProductService
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.service.ProductService
import woowacourse.shopping.view.model.event.ErrorEvent
import kotlin.concurrent.thread

class ProductRepositoryImpl : ProductRepository {
    private val mockProductService: ProductService = MockProductService()

    override fun loadPagingProducts(offset: Int): List<Product> {
        var pagingData: List<Product> = listOf()
        thread {
            pagingData = mockProductService.findPagingProducts(offset, PRODUCT_LOAD_PAGING_SIZE)
        }.join()
        if (pagingData.isEmpty()) throw ErrorEvent.MaxPagingDataEvent()
        return pagingData
    }

    override fun loadCategoryProducts(
        size: Int,
        category: String,
    ): List<Product> {
        var pagingData: List<Product> = listOf()
        thread {
            pagingData = mockProductService.findPagingProducts(DEFAULT_ITEM_SIZE, PRODUCT_LOAD_PAGING_SIZE)
        }.join()
        if (pagingData.isEmpty()) throw ErrorEvent.MaxPagingDataEvent()
        return pagingData.filter { product ->
            product.category == category
        }
    }

    override fun getProduct(productId: Long): Product {
        var product: Product? = null
        thread {
            product = mockProductService.findProductById(productId)
        }.join()
        return product ?: throw ErrorEvent.LoadDataEvent()
    }

    companion object {
        const val PRODUCT_LOAD_PAGING_SIZE = 20
        const val DEFAULT_ITEM_SIZE = 0
    }
}
