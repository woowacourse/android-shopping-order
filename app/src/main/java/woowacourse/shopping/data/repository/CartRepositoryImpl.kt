package woowacourse.shopping.data.repository

import com.example.domain.model.CartProduct
import com.example.domain.repository.CartRepository
import woowacourse.shopping.data.datasource.remote.shoppingcart.ShoppingCartDataSource
import woowacourse.shopping.mapper.toDomain

class CartRepositoryImpl(
    private val shoppingCartDataSource: ShoppingCartDataSource,
) : CartRepository {

    override fun getAllProductInCart(callback: (List<CartProduct>) -> Unit) {
        shoppingCartDataSource.getAllProductInCart {
            if (it.isSuccess) {
                val cartProducts = it.getOrNull()?.map { cartProduct -> cartProduct.toDomain() }
                callback(cartProducts ?: throw IllegalArgumentException())
            } else {
                throw IllegalArgumentException()
            }
        }
    }

    override fun insert(
        id: Long,
        quantity: Int,
        callback: (Unit) -> Unit,
    ) {
        shoppingCartDataSource.postProductToCart(id, quantity) {
            if (it.isSuccess) {
                callback(Unit)
            } else {
                throw IllegalArgumentException()
            }
        }
    }

    override fun updateCount(
        id: Long,
        count: Int,
        callback: (Unit) -> Unit,
    ) {
        shoppingCartDataSource.patchProductCount(id, count) {
            if (it.isSuccess) {
                callback(Unit)
            } else {
                throw IllegalArgumentException()
            }
        }
    }

    override fun remove(id: Long, callback: (Unit) -> Unit) {
        shoppingCartDataSource.deleteProductInCart(id) {
            if (it.isSuccess) {
                callback(Unit)
            } else {
                throw IllegalArgumentException()
            }
        }
    }

    override fun findById(id: Long, callback: (CartProduct?) -> Unit) {
        getAllProductInCart { cartProducts ->
            val cartProduct = cartProducts.find { it.id == id }
            callback(cartProduct)
        }
    }

    override fun findByProductId(productId: Long, callback: (CartProduct?) -> Unit) {
        getAllProductInCart { cartProducts ->
            val cartProduct = cartProducts.find { it.product.id == productId }
            callback(cartProduct)
        }
    }

    override fun getSubList(offset: Int, step: Int, callback: (List<CartProduct>) -> Unit) {
        getAllProductInCart { cartProducts ->
            val subList = cartProducts.subList(
                offset.coerceAtMost(cartProducts.size),
                (offset + step).coerceAtMost(cartProducts.size),
            )
            callback(subList)
        }
    }
}
