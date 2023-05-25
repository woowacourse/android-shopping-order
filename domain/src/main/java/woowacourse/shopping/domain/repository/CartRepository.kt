package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Product

interface CartRepository {
    fun addCartProduct(cartProduct: CartProduct)

    fun getAllCount(): Int

    fun getAll(onSuccess: (List<CartProduct>) -> Unit, onFailure: () -> Unit)

    fun deleteCartProduct(cartProduct: CartProduct)

    fun getTotalAmount(): Int

    fun getCartProductByProduct(product: Product): CartProduct?

    fun modifyCartProduct(cartProduct: CartProduct)

    fun getTotalPrice(): Int

    fun replaceCartProduct(prev: CartProduct, new: CartProduct)

    fun isAllCheckedInPage(page: Int, sizePerPage: Int): Boolean
}
