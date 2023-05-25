package woowacourse.shopping.data.cart

import woowacourse.shopping.data.database.dao.CartDao
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.server.CartRemoteDataSource

class CartRepositoryImpl(
    private val cartRemoteDataSource: CartRemoteDataSource,
    private val cartDao: CartDao
) : CartRepository {

    override fun addCartProduct(product: Product, onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        cartRemoteDataSource.addCartProduct(
            product.id,
            onSuccess = { onSuccess(it) },
            onFailure = { onFailure() }
        )
    }

    override fun getAllCount(onSuccess: (Int) -> Unit, onFailure: () -> Unit) {
        cartRemoteDataSource.getCartProducts(
            onSuccess = {
                onSuccess(it.size)
            },
            onFailure = { onFailure() }
        )
    }

    override fun getAll(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: () -> Unit
    ) {
        cartRemoteDataSource.getCartProducts(
            onSuccess = { onSuccess(it) },
            onFailure = { onFailure() }
        )
    }

    override fun deleteCartProduct(cartProduct: CartProduct) {
        // cartDao.deleteCartProduct(cartProduct)
        // cart = cart.removeCartProduct(cartProduct)
    }

    override fun getTotalAmount(): Int {
        return cartDao.getTotalAmount()
    }

    override fun getCartProductByProduct(product: Product): CartProduct? {
        return cartDao.selectCartProductByProduct(product)
    }

    override fun modifyCartProduct(cartProduct: CartProduct) {
        cartDao.updateCartProduct(cartProduct)
    }

    override fun getTotalPrice(): Int {
        return cartDao.getTotalPrice()
    }

    override fun replaceCartProduct(prev: CartProduct, new: CartProduct) {
        // cart = cart.replaceCartProduct(prev, new)
    }

    override fun isAllCheckedInPage(page: Int, sizePerPage: Int): Boolean {
        // val startIndex = page * sizePerPage
        // val cartInPage = cart.getSubCart(startIndex, startIndex + sizePerPage)
        // return cartInPage.cartProducts.all { it.isChecked }
        return true
    }

    override fun findId(productId: Int, onSuccess: (Int?) -> Unit, onFailure: () -> Unit) {
        getAll(
            onSuccess = { products ->
                val cartProduct = products.find { it.product.id == productId }
                onSuccess(cartProduct?.id)
            },
            onFailure = { onFailure() }
        )
    }
}
