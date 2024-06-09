package woowacourse.shopping.ui.productDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.common.UniversalViewModelFactory
import woowacourse.shopping.databinding.FragmentProductDetailBinding
import woowacourse.shopping.ui.FragmentNavigator

class ProductDetailFragment : Fragment() {
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("FragmentCartListBinding is not initialized")

    private lateinit var factory: UniversalViewModelFactory
    private lateinit var viewModel: ProductDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater)
        initBinding()
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        loadProductDetail()
        observeDetailProductDestinationId()
    }

    private fun getProductId() = arguments?.getLong(PRODUCT_ID)

    private fun initViewModel() {
        val productId = getProductId() ?: return
        factory = ProductDetailViewModel.factory(productId)
        viewModel = ViewModelProvider(this, factory)[ProductDetailViewModel::class.java]
    }

    private fun initBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.onItemChargeListener = viewModel
        binding.onProductClickListener = viewModel
    }

    private fun initToolbar() {
        binding.toolbarProductDetail.setOnMenuItemClickListener {
            navigateToMenuItem(it)
        }
    }

    private fun navigateToMenuItem(it: MenuItem) =
        when (it.itemId) {
            R.id.action_x -> {
                (requireActivity() as? FragmentNavigator)?.navigateToProductList()
                true
            }
            else -> false
        }

    private fun loadProductDetail() {
        lifecycleScope.launch {
            delay(1000)
            viewModel.loadDetailPage()
        }
    }

    private fun observeDetailProductDestinationId() {
        viewModel.detailProductDestinationId.observe(viewLifecycleOwner) {
            navigateToProductDetail(it)
        }
    }

    private fun navigateToProductDetail(id: Long) = (requireActivity() as? FragmentNavigator)?.navigateToProductDetail(id)

    private fun observeErrorMessage() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_SHORT).show()
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
