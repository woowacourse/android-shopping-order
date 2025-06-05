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
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.databinding.FragmentCartProductRecommendationBinding
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.view.cart.recommendation.adapter.RecommendationAdapter
import woowacourse.shopping.view.cart.selection.CartProductSelectionFragment
import woowacourse.shopping.view.product.detail.ProductDetailActivity

class CartProductRecommendationFragment() : Fragment() {
    private var _binding: FragmentCartProductRecommendationBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy {
        val app = requireContext().applicationContext as ShoppingApplication
        ViewModelProvider(
            this,
            CartProductRecommendationViewModelFactory(
                app.productRepository,
                app.cartProductRepository,
                app.recentProductRepository,
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
        val selectedProducts: List<CartProduct> = arguments?.getSerializable(KEY_SELECTED_IDS) as ArrayList<CartProduct>
        viewModel.initShoppingCartInfo(selectedProducts)
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

        viewModel.onSelectedProduct.observe(viewLifecycleOwner) { value ->
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

        fun newBundle(selectedCartProducts: Set<CartProduct>): Bundle =
            Bundle().apply {
                putSerializable(KEY_SELECTED_IDS, ArrayList(selectedCartProducts))
            }
    }
}
