package woowacourse.shopping.data

import com.example.domain.ProductCache
import com.example.domain.model.Product
import com.example.domain.repository.ProductRepository
import woowacourse.shopping.data.service.ProductMockWebService
import java.lang.Thread.sleep

class ProductRemoteMockRepositoryImpl(
    private val webServer: ProductMockWebService,
    private val productCache: ProductCache
) : ProductRepository {

    override fun getProducts(page: Int, onSuccess: (List<Product>) -> Unit, onFailure: () -> Unit) {
        if (productCache.productList.isEmpty()) {
            Thread {
                webServer.request(
                    onSuccess = {
                        productCache.addProducts(it)
                        sleep(2000) // 스켈레톤 확인을 위한 일시 정지
                        onSuccess(
                            productCache.getSubProducts(1, LOAD_SIZE)
                        )
                    },
                    onFailure = { }
                )
            }.start()
        } else {
            onSuccess(productCache.getSubProducts(page, LOAD_SIZE))
        }
    }

    override fun getProductById(id: Long): Product? {
        return productCache.productList.find { id == it.id }
    }

    override fun clearCache() {
        productCache.clear()
    }

    companion object {
        private const val LOAD_SIZE = 20
    }
}
