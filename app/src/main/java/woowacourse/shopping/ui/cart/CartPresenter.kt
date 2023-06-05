package woowacourse.shopping.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.CompletableFuture
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.PageUIModel
import woowacourse.shopping.utils.LogUtil

class CartPresenter(
    private val view: CartContract.View,
    private val cartRepository: CartRepository,
    private var index: Int = 0
) : CartContract.Presenter {
    private val _totalPrice = MutableLiveData<Int>(0)
    override val totalPrice: LiveData<Int> get() = _totalPrice

    private val _checkedCount = MutableLiveData<Int>(0)
    override val checkedCount: LiveData<Int> get() = _checkedCount

    private val _allCheck = MutableLiveData<Boolean>(false)
    override val allCheck: LiveData<Boolean> get() = _allCheck

    private val lock = ReentrantLock()
    init {
        cartRepository.getAll()
        setUpAllButton()
    }

    private val pageUIModel
        get() = PageUIModel(
            cartRepository.hasNextPage(),
            cartRepository.hasPrevPage(),
            cartRepository.getCurrentPage()
        )

    override fun fetchCartProducts() {
        CompletableFuture.supplyAsync {
            cartRepository.getPage(index * STEP, STEP)
        }.thenAccept { result ->
            result.onSuccess {
                view.setPage(it.map { it.toUIModel() }, pageUIModel)
                setUPTotalPrice()
                setUpCheckedCount()
                setUpAllButton()
            }.onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }

    private fun setUPTotalPrice() {
        _totalPrice.postValue(cartRepository.getTotalPrice())
    }

    private fun setUpCheckedCount() {
        _checkedCount.postValue(cartRepository.getTotalCheckedCount())
    }

    private fun setUpAllButton() {
        cartRepository.getPage(index * STEP, STEP)
            .onSuccess { _allCheck.value = it.all { product -> product.checked } }
    }

    override fun setUpProductsCheck(checked: Boolean) {
        if (lock.isLocked) return
        cartRepository.updateAllChecked(checked)
        fetchCartProducts()
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

    override fun updateItemCheck(productId: Int, checked: Boolean) {
        cartRepository.updateChecked(productId, checked)
        if (lock.isLocked) return
        lock.withLock {
            setUPTotalPrice()
            setUpCheckedCount()
            setUpAllButton()
        }
    }

    override fun getPageIndex(): Int {
        return index
    }

    override fun navigateToItemDetail(productId: Int) {
        view.navigateToItemDetail(productId)
    }

    override fun checkOutOrder() {
        CompletableFuture.supplyAsync {
            cartRepository.getChecked()
        }.thenAccept { result ->
            println(result)
            result.onSuccess { cartProducts -> view.navigateToOrder(cartProducts.map { it.id }) }
                .onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }

    companion object {
        private const val STEP = 5
    }
}
