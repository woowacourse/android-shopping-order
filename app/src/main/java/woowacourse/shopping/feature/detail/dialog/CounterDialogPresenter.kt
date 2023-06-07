package woowacourse.shopping.feature.detail.dialog

import com.example.domain.model.BaseResponse
import com.example.domain.repository.CartRepository
import woowacourse.shopping.model.ProductUiModel

class CounterDialogPresenter(
    private val view: CounterDialogContract.View,
    private val cartRepository: CartRepository,
    product: ProductUiModel,
    private var cartId: Long?,
) : CounterDialogContract.Presenter {
    override var product: ProductUiModel = product
        private set

    // 담기 버튼을 누르기 전까지 현재까지 바뀐 값을 담고 있는 변수
    override var changeCount: Int = product.count
        private set

    override fun initPresenter() {
        view.setCountState(changeCount)
    }

    override fun changeCount(count: Int) {
        changeCount = count
    }

    override fun addCart() {
        when {
            product.count == 0 && changeCount == 1 -> insertProduct()
            product.count == 0 && changeCount > 1 -> {
                cartRepository.addCartProduct(product.id) { result ->
                    when (result) {
                        is BaseResponse.SUCCESS -> {
                            product.count = 1
                            cartId = result.response
                            changeCartProductCount()
                        }
                        is BaseResponse.FAILED -> view.showFailedChangeCartCount()
                        is BaseResponse.NETWORK_ERROR -> view.showNetworkError()
                    }
                }
            }
            product.count != 0 && changeCount > 0 -> changeCartProductCount()
            product.count != 0 && changeCount == 0 -> deleteCartProduct()
        }
    }

    private fun insertProduct() {
        cartRepository.addCartProduct(product.id) { result ->
            when (result) {
                is BaseResponse.SUCCESS -> {
                    product.count = 1
                    cartId = result.response
                    view.notifyChangeApplyCount(changeCount)
                    view.exit()
                }
                is BaseResponse.FAILED -> view.showFailedChangeCartCount()
                is BaseResponse.NETWORK_ERROR -> view.showNetworkError()
            }
        }
    }

    private fun changeCartProductCount() {
        cartId?.let {
            cartRepository.changeCartProductCount(it, changeCount) { result ->
                when (result) {
                    is BaseResponse.SUCCESS -> {
                        product.count = changeCount
                        this.cartId = it
                        view.notifyChangeApplyCount(changeCount)
                        view.exit()
                    }
                    is BaseResponse.FAILED -> view.showFailedChangeCartCount()
                    is BaseResponse.NETWORK_ERROR -> view.showNetworkError()
                }
            }
        }
    }

    private fun deleteCartProduct() {
        cartId?.let {
            cartRepository.deleteCartProduct(it) { result ->
                when (result) {
                    is BaseResponse.SUCCESS -> {
                        product.count = 0
                        cartId = null
                        view.notifyChangeApplyCount(changeCount)
                        view.exit()
                    }
                    is BaseResponse.FAILED -> view.showFailedChangeCartCount()
                    is BaseResponse.NETWORK_ERROR -> view.showNetworkError()
                }
            }
        }
    }
}
