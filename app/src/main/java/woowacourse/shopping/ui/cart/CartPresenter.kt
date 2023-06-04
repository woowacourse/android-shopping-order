package woowacourse.shopping.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.lang.Thread.sleep
import java.util.concurrent.CompletableFuture
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.CartProductUIModel
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

    private val currentPage = mutableListOf<CartProductUIModel>()

    private val pageUIModel
        get() = PageUIModel(
            cartRepository.hasNextPage(index, STEP),
            cartRepository.hasPrevPage(index, STEP),
            index + 1
        )

    private val lock = ReentrantLock()

    private fun fetchCartProducts(callback: () -> Unit) {
        val completableFuture = CompletableFuture.supplyAsync {
            cartRepository.getPage(index, STEP)
        }
        sleep(100)
        completableFuture.thenAccept { result ->
            result.onSuccess {
                currentPage.clear()
                currentPage.addAll(it.toUIModel())
                callback()
            }.onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }

    private fun setUpCarts() {
        view.setPage(currentPage, pageUIModel)
    }

    private fun setUPTotalPrice() {
        _totalPrice.value = cartRepository.getTotalPrice()
    }

    private fun setUpCheckedCount() {
        _checkedCount.value = cartRepository.getTotalSelectedCount()
    }

    private fun setUpAllButton() {
        _allCheck.value = currentPage.all { it.checked }
    }

    override fun setUpView() {
        fetchCartProducts {
            setUpCarts()
            setUPTotalPrice()
            setUpCheckedCount()
            setUpAllButton()
        }
    }

    override fun setUpProductsCheck(checked: Boolean) {
        if (lock.isLocked) return
        lock.withLock {
            currentPage.map { updateItemCheck(it.id, checked) }
            setUpCarts()
            setUpCheckedCount()
            setUPTotalPrice()
            setUpAllButton()
        }
    }

    override fun moveToPageNext() {
        index += 1
        fetchCartProducts {
            setUpCarts()
            setUpAllButton()
        }
    }

    override fun moveToPagePrev() {
        index -= 1
        fetchCartProducts {
            setUpCarts()
            setUpAllButton()
        }
    }

    override fun updateItemCount(productId: Int, count: Int) {
        CompletableFuture.supplyAsync {
            cartRepository.updateCountWithProductId(productId, count)
        }.thenAccept { result ->
            result.onSuccess {
                currentPage.indexOfFirst { it.productId == productId }.takeIf { it != -1 }
                    ?.let { currentPage[it] = currentPage[it].copy(count = count) }
                fetchCartProducts {
                    setUpCheckedCount()
                    setUPTotalPrice()
                }
            }.onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }

    override fun updateItemCheck(productId: Int, checked: Boolean) {
        cartRepository.updateChecked(productId, checked)
        currentPage.indexOfFirst { it.id == productId }
            .takeIf { it != -1 }
            ?.let { currentPage[it] = currentPage[it].copy(checked = checked) }
        if (lock.isLocked) return
        lock.withLock {
            setUpCheckedCount()
            setUPTotalPrice()
            setUpAllButton()
        }
    }

    override fun removeItem(productId: Int) {
        CompletableFuture.supplyAsync {
            cartRepository.remove(productId)
        }.thenAccept { result ->
            result.onSuccess {
                currentPage.removeIf { it.id == productId }
                if (currentPage.isEmpty() && index > 0) {
                    moveToPagePrev()
                } else {
                    fetchCartProducts {
                        setUpCarts()
                        setUpCheckedCount()
                        setUPTotalPrice()
                    }
                }
            }.onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }

    override fun getPageIndex(): Int {
        return index
    }

    override fun navigateToItemDetail(productId: Int) {
        view.navigateToItemDetail(productId)
    }

    override fun navigateToOrder() {
        CompletableFuture.supplyAsync {
            cartRepository.getAll()
        }.thenAccept { result ->
            result.onSuccess { cartProducts ->
                view.navigateToOrder(cartProducts.checkedProducts.map { it.id })
            }.onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }

    companion object {
        private const val STEP = 5
    }
}
