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
import woowacourse.shopping.presentation.view.catalog.CatalogFragment.Companion.CART_UPDATE_REQUEST_KEY

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
            parentFragmentManager.setFragmentResult(CART_UPDATE_REQUEST_KEY, Bundle())
            parentFragmentManager.popBackStack()
        }
    }

    private fun navigateToCatalog() {
        parentFragmentManager.setFragmentResult(CART_UPDATE_REQUEST_KEY, Bundle())

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
