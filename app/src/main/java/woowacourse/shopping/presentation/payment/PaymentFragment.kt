package woowacourse.shopping.presentation.payment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.CartRepositoryInjector
import woowacourse.shopping.data.payment.CouponRepositoryInjector
import woowacourse.shopping.databinding.FragmentPaymentBinding
import woowacourse.shopping.presentation.base.BindingFragment
import woowacourse.shopping.presentation.cart.recommend.RecommendNavArgs
import woowacourse.shopping.presentation.navigation.ShoppingNavigator

class PaymentFragment : BindingFragment<FragmentPaymentBinding>(R.layout.fragment_payment) {
    private lateinit var adapter: CouponAdapter
    private val viewModel by viewModels<PaymentViewModel> {
        val cartRepository = CartRepositoryInjector.cartRepository()
        val couponRepository = CouponRepositoryInjector.couponRepository()
        PaymentViewModel.factory(cartRepository, couponRepository)
    }
    private val navigator by lazy { requireActivity() as ShoppingNavigator }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        viewModel.coupons.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }
        initViews()
        initAppBar()
    }

    private fun initAppBar() {
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.apply {
            title = "Cart"
            setDisplayHomeAsUpEnabled(true)
        }
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(
                    menu: Menu,
                    menuInflater: MenuInflater,
                ) {
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    if (menuItem.itemId == android.R.id.home) {
                        navigator.popBackStack()
                        return true
                    }
                    return false
                }
            },
            viewLifecycleOwner,
        )
    }

    private fun initViews() {
        adapter = CouponAdapter(viewModel, viewLifecycleOwner)
        binding?.rvCouponList?.adapter = adapter
    }

    companion object {
        val TAG: String? = PaymentFragment::class.java.canonicalName

        private const val ORDERED_PRODUCTS_KEY = "ORDERED_PRODUCTS_KEY"

        fun args(navArgs: RecommendNavArgs): Bundle =
            Bundle().apply {
                putParcelable(ORDERED_PRODUCTS_KEY, navArgs)
            }
    }
}
