package woowacourse.shopping.view.cart.recommendation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartProductRecommendationBinding
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.view.cart.recommendation.adapter.RecommendationAdapter
import woowacourse.shopping.view.cart.selection.CartProductSelectionFragment
import woowacourse.shopping.view.product.detail.ProductDetailActivity

class CartProductRecommendationFragment(
    private val productRepository: ProductRepository,
    private val cartProductRepository: CartProductRepository,
    private val recentProductRepository: RecentProductRepository,
) : Fragment() {
    private var _binding: FragmentCartProductRecommendationBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            CartProductRecommendationViewModelFactory(
                productRepository,
                cartProductRepository,
                recentProductRepository,
            ),
        )[CartProductRecommendationViewModel::class.java]
    }

    private val adapter: RecommendationAdapter by lazy {
        RecommendationAdapter(eventHandler = viewModel)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            parentFragmentManager.commit {
                replace(
                    R.id.fragment,
                    CartProductSelectionFragment::class.java,
                    null,
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCartProductRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initInformation()
        initBindings()
        initObservers()
    }

    private fun initInformation() {
        val selectedIds = arguments?.getIntArray(KEY_SELECTED_IDS)?.toSet() ?: emptySet()
        val totalPrice = arguments?.getInt(KEY_TOTAL_PRICE)
        val totalCount = arguments?.getInt(KEY_TOTAL_COUNT)
        viewModel.initShoppingCartInfo(selectedIds, totalPrice, totalCount)
    }

    private fun initBindings() {
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        binding.rvRecommendedProducts.adapter = adapter
        binding.btnOrder.setOnClickListener {
            Toast.makeText(requireContext(), R.string.finish_order, Toast.LENGTH_SHORT).show()
            viewModel.finishOrder()
            requireActivity().finish()
        }
    }

    private fun initObservers() {
        viewModel.recommendedProducts.observe(viewLifecycleOwner) { value ->
            adapter.submitList(value)
        }

        viewModel.selectedProduct.observe(viewLifecycleOwner) { value ->
            val intent = ProductDetailActivity.newIntent(requireContext(), value)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_SELECTED_IDS = "selectedIds"
        private const val KEY_TOTAL_PRICE = "totalPrice"
        private const val KEY_TOTAL_COUNT = "totalCount"

        fun newBundle(
            selectedIds: Set<Int>,
            totalPrice: Int?,
            totalCount: Int?,
        ): Bundle =
            Bundle().apply {
                putIntArray(KEY_SELECTED_IDS, selectedIds.toIntArray())
                putSerializable(KEY_TOTAL_PRICE, totalPrice)
                putSerializable(KEY_TOTAL_COUNT, totalCount)
            }
    }
}
