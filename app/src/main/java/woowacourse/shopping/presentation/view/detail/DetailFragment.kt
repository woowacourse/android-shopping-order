package woowacourse.shopping.presentation.view.detail

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentDetailBinding
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.util.getParcelableCompat
import woowacourse.shopping.presentation.view.cart.CartFragment
import woowacourse.shopping.presentation.view.common.BaseFragment
import woowacourse.shopping.presentation.view.common.ItemCounterListener

class DetailFragment :
    BaseFragment<FragmentDetailBinding>(R.layout.fragment_detail),
    DetailEventListener {
    private val viewModel: DetailViewModel by viewModels { DetailViewModel.Factory }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        initListener()

        val product = arguments.getParcelableCompat<ProductUiModel>(EXTRA_PRODUCT)
        viewModel.fetchProduct(product)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            eventListener = this@DetailFragment
            detailItemCounter.eventListener =
                object : ItemCounterListener {
                    override fun increaseQuantity(product: ProductUiModel) = viewModel.increaseQuantity()

                    override fun decreaseQuantity(product: ProductUiModel) = viewModel.decreaseQuantity()
                }
        }
    }

    private fun initObserver() {
        with(viewModel) {
            saveEvent.observe(viewLifecycleOwner) {
                navigateToScreen()
            }

            product.observe(viewLifecycleOwner) { product ->
                binding.product = product
                addRecentProduct(product)
                fetchLastViewedProduct()
            }
        }
    }

    private fun initListener() {
        binding.btnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun navigateToScreen() {
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.shopping_fragment_container, CartFragment::class.java, null)
        }
    }

    companion object {
        private const val EXTRA_PRODUCT = "product"

        fun newBundle(product: ProductUiModel) = bundleOf(EXTRA_PRODUCT to product)
    }

    override fun onRecentItemSelected(product: ProductUiModel) {
        viewModel.fetchProduct(product)
    }
}
