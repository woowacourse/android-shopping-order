package woowacourse.shopping.presentation.ui.shoppingCart.adapter

import woowacourse.shopping.domain.model.Operator

interface ShoppingCartClickListener {
    fun clickItem(position: Int)
    fun clickDelete(position: Int)
    fun clickChangeQuantity(position: Int, op: Operator)
    fun applyChangedQuantity(position: Int)
    fun checkItem(position: Int, isChecked: Boolean)
}
