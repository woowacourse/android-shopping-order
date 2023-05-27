package woowacourse.shopping.ui.cart

import android.os.Handler
import android.os.Looper
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.CartProductUIModel
import woowacourse.shopping.model.PageUIModel
import woowacourse.shopping.utils.NonNullLiveData
import woowacourse.shopping.utils.NonNullMutableLiveData

class CartPresenter(
    private val view: CartContract.View,
    private val cartRepository: CartRepository,
    private var index: Int = 0
) : CartContract.Presenter {
    private val handler = Handler(Looper.getMainLooper())

    private val _totalPrice = NonNullMutableLiveData<Int>(0)
    override val totalPrice: NonNullLiveData<Int> get() = _totalPrice

    private val _checkedCount = NonNullMutableLiveData<Int>(0)
    override val checkedCount: NonNullLiveData<Int> get() = _checkedCount

    private val _allCheck = NonNullMutableLiveData<Boolean>(false)
    override val allCheck: NonNullLiveData<Boolean> get() = _allCheck

    private val currentPage = mutableListOf<CartProductUIModel>()

    private val pageUIModel get() = PageUIModel(
        cartRepository.hasNextPage(index, STEP),
        cartRepository.hasPrevPage(index, STEP),
        index + 1
    )

    private var isChangingItemCheck = false

    private fun fetchCartProducts(callback: () -> Unit) {
        cartRepository.getPage(index, STEP) {
            currentPage.clear()
            currentPage.addAll(it.toUIModel())
            callback()
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
        if (isChangingItemCheck) { return }
        currentPage.replaceAll {
            cartRepository.updateChecked(it.id, checked)
            it.copy(checked = checked)
        }
        setUpCarts()
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
        cartRepository.updateCount(productId, count) {
            currentPage.indexOfFirst { it.id == productId }
                .takeIf { it != -1 }
                ?.let { currentPage[it] = currentPage[it].copy(count = count) }
            setUpCheckedCount()
            setUPTotalPrice()
        }
    }

    override fun updateItemCheck(productId: Int, checked: Boolean) {
        Thread {
            isChangingItemCheck = true
            cartRepository.updateChecked(productId, checked)
            handler.post {
                currentPage.indexOfFirst { it.id == productId }
                    .takeIf { it != -1 }
                    ?.let { currentPage[it] = currentPage[it].copy(checked = checked) }
                setUpCheckedCount()
                setUPTotalPrice()
                setUpAllButton()
                isChangingItemCheck = false
            }
        }.start()
    }

    override fun removeItem(productId: Int) {
        cartRepository.remove(productId) {
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
        }
    }

    override fun getPageIndex(): Int {
        return index
    }

    override fun navigateToItemDetail(productId: Int) {
        cartRepository.getAll { cartProducts ->
            cartProducts.all().first { it.product.id == productId }.toUIModel().toProduct()
                .let { view.navigateToItemDetail(it.toUIModel()) }
        }
    }

    companion object {
        private const val STEP = 5
    }
}
