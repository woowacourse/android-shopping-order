package woowacourse.shopping.ui.productDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.databinding.FragmentProductDetailBinding
import woowacourse.shopping.ui.FragmentNavigator
import woowacourse.shopping.ui.productDetail.event.ProductDetailEvent
import woowacourse.shopping.ui.util.UniversalViewModelFactory

class ProductDetailFragment : Fragment() {
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("FragmentCartListBinding is not initialized")

    private lateinit var factory: UniversalViewModelFactory
    private lateinit var viewModel: ProductDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater)

        initViewModel()

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.listener = viewModel

        return binding.root
    }

    private fun initViewModel() {
        arguments?.let {
            factory = ProductDetailViewModel.factory(productId = it.getLong(PRODUCT_ID))
        }
        viewModel = ViewModelProvider(this, factory)[ProductDetailViewModel::class.java]
    }

    private fun showSkeletonUi() {
        binding.includeProductDetailShimmer.root.visibility = View.VISIBLE
        binding.layoutProductDetail.visibility = View.GONE
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        showSkeletonUi()
        viewModel.loadAll()

        observeCurrentProduct()
        viewModel.event.observe(viewLifecycleOwner) { event ->
            when (event) {
                is ProductDetailEvent.NavigateToProductDetail ->
                    (requireActivity() as? FragmentNavigator)?.navigateToProductDetail(productId = event.productId)

                is ProductDetailEvent.AddProductToCart -> Unit
                is ProductDetailEvent.SaveProductInHistory -> Unit
                is ProductDetailEvent.Finish -> (requireActivity() as? FragmentNavigator)?.popBackStack()
            }
        }

    }

    private fun observeCurrentProduct() {
        viewModel.currentProduct.observe(viewLifecycleOwner) {
            binding.includeProductDetailShimmer.root.stopShimmer()
            binding.includeProductDetailShimmer.root.visibility = View.GONE
            binding.layoutProductDetail.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PRODUCT_ID = "productId"
        const val TAG = "ProductDetailFragment"
    }
}
