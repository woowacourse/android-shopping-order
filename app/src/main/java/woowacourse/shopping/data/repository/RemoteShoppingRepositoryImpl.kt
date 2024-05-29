package woowacourse.shopping.data.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import woowacourse.shopping.data.database.ProductClient
import woowacourse.shopping.data.mapper.toDomainModel
import woowacourse.shopping.data.model.dto.ProductResponseDto
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ShoppingItemsRepository

class RemoteShoppingRepositoryImpl : ShoppingItemsRepository {
    private val service = ProductClient.service
    var productData: ProductResponseDto? = null
    var products: List<Product>? = null

    init {
        CoroutineScope(Dispatchers.IO).launch {

            productData = service.requestProducts().execute().body()
            products = productData?.content?.map { it.toDomainModel() }
        }
    }

    override fun fetchProductsSize(): Int {
        while (true) {
            Thread.sleep(1000)
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
        Log.d("crong", "$products")
        return products?.subList(start, end) ?: emptyList()
    }

    override fun findProductItem(id: Long): Product? {
        var product: Product? = null
        Log.d("crong", "detail activity")
        Log.d("crong", "$id")
        CoroutineScope(Dispatchers.IO).launch {
            product = service.requestProduct(id).execute().body()?.toDomainModel()
        }
        while (true) {
            Log.d("crong", "$product")
            Thread.sleep(1000)
            if (product != null) {
                break
            }
        }
        Log.d("crong", "$product")

        return product
    }
}
