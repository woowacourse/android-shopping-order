package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.page.Page

typealias DomainCart = Cart

data class Cart(
    val items: List<CartProduct> = emptyList(),
    val minProductSize: Int = 0,
) {
    val checkedCount: Int = items.count { it.isChecked }
    val productCountInCart: Int = items.sumOf { it.selectedCount.value }
    val checkedProductTotalPrice: Int = items.sumOf { it.getTotalPrice(true) }

    fun select(product: Product): Cart =
        copy(items = items.map { item ->
            if (item.product.id == product.id) item.select() else item
        })

    fun unselect(product: Product): Cart =
        copy(items = items.map { item ->
            if (item.product.id == product.id) item.unselect() else item
        })

    fun selectAll(page: Page): Cart {
        val cartProductsOfPage = page.takeItems(this)
        return copy(items = items.map { item ->
            cartProductsOfPage.find { it.id == item.id }?.select() ?: item
        })
    }

    fun unselectAll(page: Page): Cart {
        val cartProductsOfPage = page.takeItems(this)
        return copy(items = items.map { item ->
            cartProductsOfPage.find { it.id == item.id }?.unselect() ?: item
        })
    }

    fun update(cart: Cart): Cart =
        copy(items = cart.items.distinctBy { it.product.id })

    fun update(cartProducts: List<CartProduct>): Cart =
        copy(items = cartProducts.distinctBy { it.productId })

    fun delete(cartProduct: CartProduct): Cart =
        copy(items = items.filter { it.productId != cartProduct.productId })

    fun findCartProductByProductId(productId: Int): CartProduct? =
        items.find { it.productId == productId }

    fun updateProductCount(cartProduct: DomainCartProduct, count: Int): Cart {
        return copy(items = items.map { item ->
            if (item.productId == cartProduct.productId) item.changeCount(count) else item
        })
    }

    fun getCheckedCartItems(): List<CartProduct> =
        items.filter { it.isChecked }

    operator fun plus(cart: Cart): Cart =
        copy(items = (this.items + cart.items).distinctBy { it.product.id })

    operator fun plus(cartProducts: List<CartProduct>): Cart =
        copy(items = (this.items + cartProducts).distinctBy { it.product.id })
}
