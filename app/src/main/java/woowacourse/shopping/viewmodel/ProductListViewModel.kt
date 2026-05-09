package woowacourse.shopping.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.model.Product
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.QuantityRepository
import woowacourse.shopping.repository.ShoppingCartRepository

class ProductListViewModel(
    private val productRepository: ProductRepository = ShoppingApplication.productRepository,
    private val shoppingCartRepository: ShoppingCartRepository = ShoppingApplication.shoppingCartRepository,
    private val quantityRepository: QuantityRepository = ShoppingApplication.quantityRepository,
) : ViewModel() {
    val quantityByProductId: StateFlow<Map<Long, Int>> = quantityRepository.quantities

    fun getProducts(): List<Product> = productRepository.getProducts()

    fun addProductToCart(product: Product) {
        shoppingCartRepository.add(product)
        quantityRepository.plusQuantity(product.id)
    }

    fun increaseProductQuantity(productId: Long) {
        quantityRepository.plusQuantity(productId)
    }

    fun decreaseProductQuantity(productId: Long) {
        if (quantityRepository.getQuantity(productId) == 0) {
            return
        }
        quantityRepository.minusQuantity(productId)
    }
}
