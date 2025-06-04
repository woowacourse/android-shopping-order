package woowacourse.shopping.presentation.view.detail

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.RepositoryProvider
import woowacourse.shopping.databinding.FragmentDetailBinding
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.extension.getParcelableCompat
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.cart.CartFragment

class DetailFragment : BaseFragment<FragmentDetailBinding>(R.layout.fragment_detail) {
    private val viewModel: DetailViewModel by viewModels {
        DetailViewModel.factory(
            cartRepository = RepositoryProvider.cartRepository,
            productRepository = RepositoryProvider.productRepository,
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        initListener()

        val product = arguments.getParcelableCompat<ProductUiModel>(EXTRA_PRODUCT)
        product.let { viewModel.fetchProduct(it) }

        viewModel.fetchLastViewedProduct(product.id)
    }

    private fun initObserver() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel
        binding.detailItemCounter.listener = viewModel

        viewModel.saveState.observe(viewLifecycleOwner) { saveState ->
            saveState?.let { navigateToCatalog() }
        }

        viewModel.amount.observe(viewLifecycleOwner) { amount ->
            binding.detailItemCounter.textViewDetailAmount.text = amount.toString()
        }

        viewModel.product.observe(viewLifecycleOwner) { productUiModel ->
            binding.product = productUiModel
        }
        viewModel.lastViewedProduct.observe(viewLifecycleOwner) { recentProduct ->
            binding.viewDetailLastViewed.setOnClickListener {
                viewModel.loadProductById(recentProduct.id)
            }
        }
    }

    private fun initListener() {
        binding.btnClose.setOnClickListener {
            parentFragmentManager.setFragmentResult("cart_update_result", Bundle())
            parentFragmentManager.popBackStack()
        }
    }

    private fun navigateToCatalog() {
        parentFragmentManager.setFragmentResult("cart_update_result", Bundle())

        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.shopping_fragment_container, CartFragment::class.java, null)
        }
    }

    companion object {
        private const val EXTRA_PRODUCT = "product"

        fun newBundle(product: ProductUiModel) =
            bundleOf(
                EXTRA_PRODUCT to product,
            )
    }
}
