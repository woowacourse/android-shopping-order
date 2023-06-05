package woowacourse.shopping.model.data.repository

import com.shopping.domain.CartProduct
import com.shopping.domain.Count
import com.shopping.domain.Product
import com.shopping.repository.CartProductRepository
import woowacourse.shopping.model.data.dto.CartProductDTO
import woowacourse.shopping.model.data.dto.ProductDTO
import woowacourse.shopping.server.retrofit.CartItemsService
import woowacourse.shopping.server.retrofit.createResponseCallback

class CartProductRepositoryImpl(
    private val service: CartItemsService
) : CartProductRepository {
    private var cartProducts = emptyList<CartProduct>()

    init {
        fetchCartProducts()
    }

    override fun getAll(onSuccess: (List<CartProduct>) -> Unit, onFailure: (Exception) -> Unit) {
        service.getCartItems().enqueue(
            createResponseCallback(
                onSuccess = { received ->
                    cartProducts = received.map { it.toDomain() }
                    onSuccess(cartProducts)
                },
                onFailure = { throw IllegalStateException("서버 통신 실패") }
            )
        )
    }

    override fun getAllProductsCount(): Int {
        return cartProducts.sumOf { cartProduct ->
            cartProduct.count.value
        }
    }

    override fun loadCartProducts(index: Pair<Int, Int>): List<CartProduct> {
        if (index.first >= cartProducts.size) {
            return emptyList()
        }
        return cartProducts.subList(index.first, minOf(index.second, cartProducts.size))
    }

    override fun update(cartProduct: CartProduct) {
        service.patchCartItem(cartProduct.id, cartProduct.count.value)
        fetchCartProducts()
    }

    override fun updateCount(product: Product, count: Int, updateCartBadge: () -> Unit) {
        service.patchCartItem(product.id, count).enqueue(
            createResponseCallback(
                onSuccess = {
                    updateCartBadge()
                },
                onFailure = { throw IllegalStateException("update 실패") }
            )
        )
        fetchCartProducts()
    }

    override fun add(product: Product) {
        service.addCartItem(product.id).enqueue(
            createResponseCallback(
                onSuccess = {
                    println("성공")
                },
                onFailure = { throw IllegalStateException("add 실패") }
            )
        )
        fetchCartProducts()
    }

    override fun findCountById(id: Long): Int {
        val cartProduct = cartProducts.find {
            it.product.id == id
        } ?: return 0
        return cartProduct.count.value
    }

    override fun getTotalPrice(): Int =
        cartProducts.filter { it.isSelected }.sumOf { it.product.price * it.count.value }

    override fun getTotalCount(): Int =
        cartProducts.filter { it.isSelected }.sumOf { it.count.value }

    override fun remove(cartProduct: CartProduct) {
        service.deleteCartItem(cartProduct.id)
        fetchCartProducts()
    }

    override fun getCheckedCartItems(): Array<Long> {
        val cartItemIds = mutableListOf<Long>()
        cartProducts.forEach { cartProduct ->
            if (cartProduct.isSelected) {
                cartItemIds.add(cartProduct.id)
            }
        }
        return cartItemIds.toTypedArray()
    }

    private fun fetchCartProducts() {
        getAll(onSuccess = { }, onFailure = { })
    }

    private fun ProductDTO.toDomain(): Product =
        Product(id, name, imageUrl, price)

    private fun CartProductDTO.toDomain(): CartProduct =
        CartProduct(id, product.toDomain(), Count(quantity), true)
}
