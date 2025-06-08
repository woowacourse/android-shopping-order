package woowacourse.shopping.presentation.view.checkout

import android.os.Bundle
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCheckoutBinding
import woowacourse.shopping.presentation.view.common.BaseFragment

class CheckoutFragment : BaseFragment<FragmentCheckoutBinding>(R.layout.fragment_checkout) {
    private val viewModel: CheckoutViewModel by viewModels { CheckoutViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadCoupons()
    }
}
