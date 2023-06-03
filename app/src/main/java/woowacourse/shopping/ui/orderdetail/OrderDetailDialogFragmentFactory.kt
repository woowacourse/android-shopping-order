package woowacourse.shopping.ui.orderdetail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import woowacourse.shopping.ui.model.UiOrder

class OrderDetailDialogFragmentFactory(
    private val orderInfo: UiOrder,
    private val dismissListener: (() -> Unit)?
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            OrderDetailDialog::class.java.name -> OrderDetailDialog(orderInfo, dismissListener)
            else -> super.instantiate(classLoader, className)
        }
    }
}
