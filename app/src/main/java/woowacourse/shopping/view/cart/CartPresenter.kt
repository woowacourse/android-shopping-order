package woowacourse.shopping.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import woowacourse.shopping.domain.cartsystem.CartPageStatus
import woowacourse.shopping.domain.cartsystem.CartSystem
import woowacourse.shopping.domain.cartsystem.CartSystemResult
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.pagination.CartPagination
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.model.CartProductModel
import woowacourse.shopping.model.toDomain
import woowacourse.shopping.model.toUiModel

class CartPresenter(
    private val view: CartContract.View,
    private val cartRepository: CartRepository,
) : CartContract.Presenter {
    private lateinit var cartPagination: CartPagination
    private val cartSystem = CartSystem()
    private val cartItems: MutableList<CartViewItem> = mutableListOf()

    private var _cartSystemResult = MutableLiveData(CartSystemResult(0, 0))
    override val cartSystemResult: LiveData<CartSystemResult>
        get() = _cartSystemResult
    private var _isCheckedAll = MutableLiveData(false)
    override val isCheckedAll: LiveData<Boolean>
        get() = _isCheckedAll

    private var cartPageStatus =
        CartPageStatus(isPrevEnabled = false, isNextEnabled = false, 1)

    override fun fetchProducts() {
        cartRepository.getAll {
            cartPagination = CartPagination(PAGINATION_SIZE, it)
            cartPagination.fetchNextItems { cartProducts ->
                val items = cartProducts.map {
                    CartViewItem.CartProductItem(
                        it.toUiModel(cartSystem.isSelectedProduct(it)),
                    )
                }
                cartItems.addAll(items)
                _isCheckedAll.value = getIsCheckedAll()
                addBottomPagination()
                view.showProducts(cartItems)
                view.stopLoading()
            }
        }
    }

    private fun addBottomPagination() {
        cartPageStatus = cartPagination.status
        cartItems.add(CartViewItem.PaginationItem(cartPageStatus))
    }

    override fun fetchNextPage() {
        cartItems.clear()
        cartPagination.fetchNextItems { cartProducts ->
            fetchNewItems(cartProducts)
        }
    }

    override fun fetchPrevPage() {
        cartItems.clear()
        cartPagination.fetchPrevItems { cartProducts ->
            fetchNewItems(cartProducts)
        }
    }

    private fun fetchCurrentPage() {
        cartItems.clear()
        cartPagination.fetchCurrentItems { cartProducts ->
            fetchNewItems(cartProducts)
        }
    }

    private fun fetchNewItems(cartProducts: List<CartProduct>) {
        val items = cartProducts.map {
            CartViewItem.CartProductItem(
                it.toUiModel(
                    cartSystem.isSelectedProduct(it),
                ),
            )
        }
        cartItems.addAll(items)
        addBottomPagination()
        _isCheckedAll.postValue(getIsCheckedAll())
        view.showChangedItems()
    }

    override fun removeProduct(cartId: Int) {
        cartRepository.remove(cartId) { isSuccess ->
            if (!isSuccess) return@remove
            val item =
                cartItems.first { it is CartViewItem.CartProductItem && it.product.cartId == cartId } as CartViewItem.CartProductItem
            val nextItem = cartPagination.removeItem(item.product.toDomain())
            cartItems.remove(item)

            _cartSystemResult.postValue(cartSystem.removeProduct(cartId))
            _isCheckedAll.postValue(getIsCheckedAll())

            cartItems.removeLast()
            if (nextItem != null) addNextProduct(nextItem)
            if (cartItems.isEmpty()) {
                fetchCurrentPage()
                return@remove
            }
            addBottomPagination()
            view.showChangedItems()
        }
    }

    private fun addNextProduct(nextCartProduct: CartProduct) {
        val model = nextCartProduct.toUiModel(cartSystem.isSelectedProduct(nextCartProduct))
        cartItems.add(CartViewItem.CartProductItem(model))
    }

    private fun getIsCheckedAll() =
        cartSystem.selectedProducts.containsAll(convertItemsToCartProducts(cartItems))

    override fun checkProductsAll() {
        val isChecked = _isCheckedAll.value?.not() ?: true

        cartItems.filterIsInstance<CartViewItem.CartProductItem>().forEachIndexed { index, item ->
            val newItem = item.product.copy(isChecked = isChecked)
            cartItems[index] = CartViewItem.CartProductItem(newItem)
            view.showChangedItem(index)
        }

        val products = if (isChecked) { // 전체 선택
            convertItemsToCartProducts(cartItems) - cartSystem.selectedProducts.toSet()
        } else { // 전체 해제
            convertItemsToCartProducts(cartItems).intersect(cartSystem.selectedProducts.toSet())
        }
        var result: CartSystemResult? = _cartSystemResult.value
        products.forEach {
            result = cartSystem.selectProduct(it)
        }
        _cartSystemResult.value = result
        _isCheckedAll.value = isChecked
    }

    override fun checkProduct(product: CartProductModel) {
        _cartSystemResult.value = cartSystem.selectProduct(product.toDomain())
        val item = cartItems.filterIsInstance<CartViewItem.CartProductItem>()
            .first { it.product.cartId == product.cartId }
        val index = cartItems.indexOf(item)
        val newProduct = item.product.copy(isChecked = !item.product.isChecked)
        cartItems[index] = CartViewItem.CartProductItem(newProduct)
        _isCheckedAll.value = getIsCheckedAll()
    }

    override fun updateCartProductCount(cartId: Int, quantity: Int) {
        if (quantity < COUNT_MIN) return
        cartRepository.update(cartId, quantity) { isSuccess ->
            if (!isSuccess) return@update
            val cartProducts = convertItemsToCartProducts(cartItems)
            val index = cartProducts.indexOfFirst { it.id == cartId }
            val newProduct =
                (cartItems[index] as CartViewItem.CartProductItem).product.copy(quantity = quantity)
            cartItems[index] = CartViewItem.CartProductItem(newProduct)
            cartPagination.updateItem(cartId, quantity)
            cartSystem.updateProduct(cartId, quantity)
            view.showChangedItem(index)
            _cartSystemResult.postValue(cartSystem.updateProduct(cartId, quantity))
            view.showChangedItem(index)
        }
    }

    override fun order() {
        if (cartSystem.selectedProducts.isEmpty()) {
            view.showProductsNothingToast()
            return
        }
        view.showOrderActivity(cartSystem.selectedProducts)
    }

    private fun convertItemsToCartProducts(items: List<CartViewItem>): List<CartProduct> =
        items.filterIsInstance<CartViewItem.CartProductItem>().map { it.product.toDomain() }

    companion object {
        private const val PAGINATION_SIZE = 5
        private const val COUNT_MIN = 1
    }
}
