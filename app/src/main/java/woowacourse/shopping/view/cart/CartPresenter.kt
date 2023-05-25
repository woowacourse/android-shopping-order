package woowacourse.shopping.view.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import woowacourse.shopping.domain.cartsystem.CartPageStatus
import woowacourse.shopping.domain.cartsystem.CartSystem
import woowacourse.shopping.domain.cartsystem.CartSystemResult
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.model.CartProductModel
import woowacourse.shopping.model.toDomain
import woowacourse.shopping.model.toUiModel

class CartPresenter(
    private val view: CartContract.View,
    private val cartRepository: CartRepository,
) : CartContract.Presenter {
    //    private val cartPagination = CartPagination(PAGINATION_SIZE, cartRepository)
    private val cartSystem = CartSystem()
    private val cartItems: MutableList<CartViewItem> = mutableListOf()

    private var _cartSystemResult = MutableLiveData(CartSystemResult(0, 0))
    private var _cartPageStatus =
        CartPageStatus(
            isPrevEnabled = false,
            isNextEnabled = false,
            1,
        )
    private var _isCheckedAll = MutableLiveData(false)

    override val cartSystemResult: LiveData<CartSystemResult>
        get() = _cartSystemResult
    override val isCheckedAll: LiveData<Boolean>
        get() = _isCheckedAll

    private var mark = 0

    init {
        cartRepository.findRange(mark, PAGINATION_SIZE) { cartProducts ->
            checkStatus()
            cartItems.addAll(cartProducts.map { CartViewItem.CartProductItem(it.toUiModel()) })

            mark += cartProducts.size
            cartRepository.isExistByMark(mark) {
                _cartPageStatus = CartPageStatus(false, it, 1)
                cartItems.add(CartViewItem.PaginationItem(_cartPageStatus))
            }
        }
    }

    private fun checkStatus() {
        cartRepository.isExistByMark(mark) { nextEnabled ->
            cartRepository.isExistByMark(mark - PAGINATION_SIZE - 1) { prevEnabled ->
                _cartPageStatus = CartPageStatus(
                    nextEnabled,
                    prevEnabled,
                    mark / PAGINATION_SIZE,
                )
            }
        }
    }

    override fun fetchProducts() {
        view.showProducts(cartItems)
    }

    override fun removeProduct(id: Int) {
        val nextItemExist = _cartPageStatus.isNextEnabled
        cartRepository.remove(id) { isSuccess ->
            if (!isSuccess) return@remove
            cartItems.removeLast()
            cartItems.removeIf { it is CartViewItem.CartProductItem && it.product.cartId == id }
            _cartSystemResult.postValue(cartSystem.removeProduct(id))
            _isCheckedAll.postValue(getIsCheckedAll())

            // 남은 자리 페이지 뒷 상품으로 다시 채우기
            if (nextItemExist) {
                addNextProduct()
                return@remove
            }
            view.showChangedItems()
        }
    }

    private fun addNextProduct() {
        cartRepository.findRange(mark - 1, 1) { products ->
            val cartProductModel = products[0].toUiModel(cartSystem.isSelectedProduct(products[0]))
            cartItems.add(CartViewItem.CartProductItem(cartProductModel))
            cartRepository.isExistByMark(mark) {
                _cartPageStatus = _cartPageStatus.copy(isNextEnabled = it)
                cartItems.add(CartViewItem.PaginationItem(_cartPageStatus))
                _isCheckedAll.postValue(getIsCheckedAll())
                view.showChangedItems()
            }
        }
    }

    private fun getIsCheckedAll() =
        cartSystem.selectedProducts.containsAll(convertItemsToCartProducts(cartItems))

    override fun checkProductsAll() {
        val isChecked = _isCheckedAll.value?.not() ?: true

        cartItems.filterIsInstance<CartViewItem.CartProductItem>().forEachIndexed { index, it ->
            it.product.isChecked = isChecked
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
            .first { it.product.id == product.id }
        item.product.isChecked = !item.product.isChecked
        _isCheckedAll.value = getIsCheckedAll()
    }

    override fun fetchNextPage() {
        cartRepository.findRange(mark, PAGINATION_SIZE) { items ->
            if (items.isNotEmpty()) {
                mark += items.size
                cartRepository.isExistByMark(mark) { isExist ->
                    _cartPageStatus = CartPageStatus(true, isExist, _cartPageStatus.count + 1)
                    changeListItems(items)
                    view.showChangedItems()
                }
            }
            _isCheckedAll.postValue(getIsCheckedAll())
        }
    }

    override fun fetchPrevPage() {
        mark -= cartItems.filterIsInstance<CartViewItem.CartProductItem>().size
        cartRepository.findRange(mark - PAGINATION_SIZE, PAGINATION_SIZE) { items ->
            val isExist: Boolean = mark - PAGINATION_SIZE - 1 >= 0
            if (items.isNotEmpty()) {
                _cartPageStatus = CartPageStatus(isExist, true, _cartPageStatus.count - 1)
                changeListItems(items)
                view.showChangedItems()
            }
            _isCheckedAll.postValue(getIsCheckedAll())
        }
    }

    private fun changeListItems(items: List<CartProduct>) {
        val models = convertCartProductToModels(items)
        cartItems.clear()
        cartItems.addAll(models.map { CartViewItem.CartProductItem(it) })
        cartItems.add(CartViewItem.PaginationItem(_cartPageStatus))
    }

    override fun updateCartProductCount(cartId: Int, count: Int) {
        if (count < COUNT_MIN) return
        cartRepository.update(cartId, count) { isSuccess ->
            val cartProducts = convertItemsToCartProducts(cartItems)
            cartProducts.find { it.cartId == cartId }?.let {
                val index = cartProducts.indexOf(it)
                (cartItems[index] as CartViewItem.CartProductItem).product.count = count
                view.showChangedItem(index)
                _cartSystemResult.postValue(cartSystem.updateProduct(it.product.id, count))
            }
        }
    }

    private fun convertCartProductToModels(cartProducts: List<CartProduct>) =
        cartProducts.map {
            it.toUiModel(cartSystem.isSelectedProduct(it))
        }.toMutableList()

    private fun convertItemsToCartProducts(items: List<CartViewItem>): List<CartProduct> =
        items.filterIsInstance<CartViewItem.CartProductItem>().map { it.product.toDomain() }

    companion object {
        private const val PAGINATION_SIZE = 5
        private const val COUNT_MIN = 1
    }
}
