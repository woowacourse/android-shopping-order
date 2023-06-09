package woowacourse.shopping.presentation.ui.shoppingCart

import woowacourse.shopping.domain.model.Operator
import woowacourse.shopping.presentation.ui.shoppingCart.adapter.ShoppingCartClickListener

class ShoppingCartClickListenerImpl(private val presenter: ShoppingCartContract.Presenter) :
    ShoppingCartClickListener {
    override fun checkItem(position: Int, isChecked: Boolean) {
        presenter.changeSelection(position, isChecked)
    }

    override fun clickChangeQuantity(position: Int, op: Operator) {
        presenter.updateProductQuantity(position, op)
    }

    override fun applyChangedQuantity(position: Int) {
        presenter.applyQuantityChanged(position)
    }

    override fun clickDelete(position: Int) {
        presenter.deleteProductInCart(position)
    }

    override fun clickItem(position: Int) {
        presenter.showProductDetail(position)
    }
}
