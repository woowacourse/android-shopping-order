package woowacourse.shopping.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.CompletableFuture
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.CartProductPageUIModel
import woowacourse.shopping.utils.LogUtil

class CartPresenter(
    private val view: CartContract.View,
    private val cartRepository: CartRepository,
    private var index: Int = 0
) : CartContract.Presenter {
    private val _page = MutableLiveData<CartProductPageUIModel>()
    override val page: LiveData<CartProductPageUIModel> get() = _page

    private val _totalPrice = MutableLiveData<Int>(0)
    override val totalPrice: LiveData<Int> get() = _totalPrice

    private val _checkedCount = MutableLiveData<Int>(0)
    override val checkedCount: LiveData<Int> get() = _checkedCount

    private val _allCheck = MutableLiveData<Boolean>(false)
    override val allCheck: LiveData<Boolean> get() = _allCheck

    private var isCheckChanging = false

    init {
        CompletableFuture.supplyAsync { cartRepository.getAll() }.get()
        setUpAllButton()
    }

    override fun fetchCartProducts() {
        CompletableFuture.supplyAsync {
            cartRepository.getPage(index * STEP, STEP)
        }.thenAccept { result ->
            result.onSuccess {
                setUpPage(it.toUIModel())
                setUPTotalPrice()
                setUpCheckedCount()
                setUpAllButton()
            }.onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }

    override fun moveToPageNext() {
        index += 1
        cartRepository.getPage(index * STEP, STEP)
        fetchCartProducts()
    }

    override fun moveToPagePrev() {
        index -= 1
        cartRepository.getPage(index * STEP, STEP)
        fetchCartProducts()
    }

    override fun updateItemCount(productId: Int, count: Int) {
        CompletableFuture.supplyAsync {
            cartRepository.updateCountWithProductId(productId, count)
        }.thenAccept { result ->
            result.onSuccess { fetchCartProducts() }
                .onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }

    override fun updateItemsCheck(checked: Boolean) {
        if (isCheckChanging) return
        page.value?.cartProducts?.map { it.id }
            ?.let { cartRepository.updateChecked(it, checked) }
        fetchCartProducts()
    }

    override fun updateItemCheck(productId: Int, checked: Boolean) {
        cartRepository.updateChecked(productId, checked)
        if (isCheckChanging) return
        isCheckChanging = true
        setUPTotalPrice()
        setUpCheckedCount()
        setUpAllButton()
        isCheckChanging = false
    }

    override fun getPageIndex(): Int {
        return index
    }

    override fun processToItemDetail(productId: Int) {
        view.navigateToItemDetail(productId)
    }

    override fun processToOrderCheckout() {
        CompletableFuture.supplyAsync {
            cartRepository.getChecked()
        }.thenAccept { result ->
            result.onSuccess { cartProducts ->
                view.navigateToOrderCheckout(cartProducts.map { it.id })
            }.onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }

    private fun setUpPage(cartProductPage: CartProductPageUIModel) {
        _page.postValue(cartProductPage)
    }

    private fun setUPTotalPrice() {
        _totalPrice.postValue(cartRepository.getTotalCheckedPrice())
    }

    private fun setUpCheckedCount() {
        _checkedCount.postValue(cartRepository.getTotalCheckedQuantity())
    }

    private fun setUpAllButton() {
        _allCheck.value = cartRepository.getPage(index * STEP, STEP)
            .getOrNull()?.cartProducts
            ?.all { it.checked } ?: false
    }

    companion object {
        private const val STEP = 5
    }
}
