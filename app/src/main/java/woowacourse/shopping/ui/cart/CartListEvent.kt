package woowacourse.shopping.ui.cart

import woowacourse.shopping.ui.customview.CounterEvent

interface CartListEvent : CounterEvent {
    fun onClickCloseButton(id: Long)
    fun onClickCheckBox(id: Long, isChecked: Boolean)
}
