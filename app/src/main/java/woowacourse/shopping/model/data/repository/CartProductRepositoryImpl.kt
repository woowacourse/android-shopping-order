package woowacourse.shopping.model.data.repository

import com.shopping.domain.CartProduct
import com.shopping.domain.OrderProduct
import com.shopping.domain.Product
import com.shopping.repository.CartProductRepository
import woowacourse.shopping.model.data.dto.RequestCartDTO
import woowacourse.shopping.model.uimodel.mapper.toDomain
import woowacourse.shopping.model.uimodel.mapper.transformToOrderProduct
import woowacourse.shopping.server.retrofit.CartItemsService
import woowacourse.shopping.server.retrofit.createResponseCallback

class CartProductRepositoryImpl(
    private val service: CartItemsService
) : CartProductRepository {
    private var _cartProducts = emptyList<CartProduct>()
    override val cartProducts
        get() = _cartProducts.toList()

    init {
        fetchCartProducts()
    }

    override fun getAll(onSuccess: (List<CartProduct>) -> Unit, onFailure: (Exception) -> Unit) {
        service.getCartItems().enqueue(
            createResponseCallback(
                onSuccess = { received ->
                    _cartProducts = received.map { it.toDomain() }
                    onSuccess(_cartProducts)
                },
                onFailure = { throw IllegalStateException("서버 통신 실패") }
            )
        )
    }

    override fun loadCartProducts(index: Pair<Int, Int>): List<CartProduct> {
        if (index.first >= _cartProducts.size) {
            return emptyList()
        }
        return _cartProducts.subList(index.first, minOf(index.second, _cartProducts.size))
    }

    override fun update(cartProduct: CartProduct) {
        service.patchCartItem(cartProduct.id, cartProduct.count.value).enqueue(
            createResponseCallback(
                onSuccess = {
                    fetchCartProducts()
                },
                onFailure = {
                    throw IllegalStateException("상품 갯수 업데이트에 실패했습니다.")
                }
            )
        )
    }

    override fun updateCount(product: Product, count: Int, updateCartBadge: () -> Unit) {
        val cartProduct = _cartProducts.find { it.product.id == product.id } ?: throw IllegalStateException("상품을 찾을 수 없습니다.")
        service.patchCartItem(cartProduct.id, count).enqueue(
            createResponseCallback(
                onSuccess = {
                    updateCartBadge()
                },
                onFailure = { throw IllegalStateException("update 실패") }
            )
        )
        fetchCartProducts()
    }

    override fun add(product: Product, quantity: Int) {
        service.addCartItem(RequestCartDTO(product.id, quantity)).enqueue(
            createResponseCallback(
                onSuccess = {
                    println("성공")
                },
                onFailure = { throw IllegalStateException("add 실패") }
            )
        )
        fetchCartProducts()
    }

    override fun remove(cartProduct: CartProduct) {
        service.deleteCartItem(cartProduct.id).enqueue(
            createResponseCallback(
                onSuccess = {
                    fetchCartProducts()
                },
                onFailure = {
                    throw IllegalStateException("상품 삭제를 실패하였습니다. : ${it.message}")
                }
            )
        )
    }

    override fun getCartItemsWithIds(cartItemIds: List<Long>, onSuccess: (List<OrderProduct>) -> Unit) {
        service.getCartItems().enqueue(
            createResponseCallback(
                onSuccess = { cartProducts ->
                    val orderProducts = mutableListOf<OrderProduct>()
                    cartItemIds.forEach { cartId ->
                        val orderProduct = cartProducts.find { cartProduct ->
                            cartProduct.id == cartId
                        } ?: throw IllegalStateException("해당 상품을 찾을 수 없습니다.")
                        orderProducts.add(orderProduct.transformToOrderProduct())
                    }
                    onSuccess(orderProducts)
                },
                onFailure = {
                    throw IllegalStateException("상품을 불러오는데 실패했습니다.")
                }
            )
        )
    }

    private fun fetchCartProducts() {
        getAll(onSuccess = { }, onFailure = { })
    }
}
