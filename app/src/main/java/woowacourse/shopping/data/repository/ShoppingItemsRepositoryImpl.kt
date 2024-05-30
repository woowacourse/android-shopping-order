package woowacourse.shopping.data.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import woowacourse.shopping.data.database.ProductClient
import woowacourse.shopping.data.database.product.ProductDatabase
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.model.dto.ProductResponseDto
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductsRepository
import woowacourse.shopping.domain.repository.ShoppingItemsRepository
import woowacourse.shopping.domain.service.RetrofitService
import java.lang.Thread.sleep

class ShoppingItemsRepositoryImpl(
    productsRepository: ProductsRepository = ProductDatabase,
) : ShoppingItemsRepository {
    private val service = ProductClient.client.create(RetrofitService::class.java)
    var productData: ProductResponseDto? = null
    var products: List<Product>? = null

    init {
        CoroutineScope(Dispatchers.IO).launch {

            productData = service.requestProducts().execute().body()
            products = productData?.content?.map { it.toDomainModel() }
        }
    }

    override fun fetchProductsSize(): Int {
        /*var size = 0
        threadAction {
            size = service.fetchProductsSize()
        }*/
        while (true) {
            sleep(1000)
            if (products != null) {
                break
            }
        }
        Log.d("crong", "${productData?.totalElements}")
        return productData?.totalElements ?: 0
    }

    override fun fetchProductsWithIndex(
        start: Int,
        end: Int,
    ): List<Product> {
        /*var products = emptyList<Product>()
        threadAction {
            products = productService.loadPagingProducts(start, end - start)
        }
        return products*/
        Log.d("crong", "$products")
        return products?.subList(start, end) ?: emptyList()
    }

    override fun findProductItem(id: Long): Product? {
        /*var product: Product? = null
        threadAction {
            product = productService.findProductById(id)
        }
        return product*/
        var product: Product? = null
        Log.d("crong", "detail activity")
        Log.d("crong", "$id")
        CoroutineScope(Dispatchers.IO).launch {
            product = service.requestProduct(id).execute().body()?.toDomainModel()
        }
        while (true) {
            Log.d("crong", "$product")
            sleep(1000)
            if (product != null) {
                break
            }
        }
        Log.d("crong", "$product")

        return product
    }

    override fun recommendProducts(
        category: String,
        count: Int,
        cartItemIds: List<Long>,
    ): List<Product> {
        return listOf()
    }

    private fun threadAction(action: () -> Unit) {
        val thread = Thread(action)
        thread.start()
        thread.join()
    }
}
