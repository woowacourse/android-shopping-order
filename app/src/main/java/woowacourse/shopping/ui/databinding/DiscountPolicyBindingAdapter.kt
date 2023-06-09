package woowacourse.shopping.ui.databinding

import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import woowacourse.shopping.databinding.ItemOrderDiscountBinding
import woowacourse.shopping.ui.order.uistate.DiscountPolicyUIState

object DiscountPolicyBindingAdapter {
    @BindingAdapter("app:discountPolicies")
    @JvmStatic
    fun discountPolicies(layout: LinearLayout, discountPolicies: List<DiscountPolicyUIState>?) {
        discountPolicies ?: return
        for (discountPolicy in discountPolicies) {
            val discountPolicyBinding = ItemOrderDiscountBinding.inflate(
                LayoutInflater.from(layout.context), layout, true
            )
            discountPolicyBinding.discountPolicy = discountPolicy
        }
    }
}
