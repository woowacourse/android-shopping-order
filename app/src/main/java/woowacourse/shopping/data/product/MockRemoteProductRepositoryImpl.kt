package woowacourse.shopping.data.product

import com.example.domain.Product
import com.example.domain.repository.ProductRepository

class MockRemoteProductRepositoryImpl(
    private val url: String,
    private val service: MockProductRemoteService
) : ProductRepository {

    override fun getAll(
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    ) {
        Thread {
            service.requestAllProducts(
                url = url,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        }.start()
    }

    override fun getProduct(
        id: Int,
        onSuccess: (product: Product?) -> Unit,
        onFailure: () -> Unit
    ) {
        Thread {
            service.requestProduct(
                url = url,
                id = id,
                onSuccess = onSuccess,
                onFailure = onFailure,
            )
        }
    }

    override fun addProduct(
        name: String,
        price: Int,
        imageUrl: String,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    ) {
    }

    override fun updateProduct(
        product: Product,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    ) {
    }

    override fun deleteProduct(id: Int, onSuccess: (List<Product>) -> Unit, onFailure: () -> Unit) {
    }
}
