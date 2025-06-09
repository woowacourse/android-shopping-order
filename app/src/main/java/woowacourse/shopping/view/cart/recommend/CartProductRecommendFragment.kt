package woowacourse.shopping.view.cart.recommend

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.databinding.FragmentCartProductRecommendBinding
import woowacourse.shopping.domain.model.CartProducts
import woowacourse.shopping.view.cart.recommend.adapter.RecommendedProductAdapter
import woowacourse.shopping.view.cart.select.CartProductSelectFragment
import woowacourse.shopping.view.payment.PaymentActivity
import woowacourse.shopping.view.product.detail.ProductDetailActivity
import woowacourse.shopping.view.util.getSerializableCompat

class CartProductRecommendFragment : Fragment() {
    private var _binding: FragmentCartProductRecommendBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CartProductRecommendViewModel

    private val adapter: RecommendedProductAdapter by lazy { RecommendedProductAdapter(viewModel) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            parentFragmentManager.setFragmentResult(
                CartProductSelectFragment.KET_FRAGMENT_RESULT,
                Bundle.EMPTY,
            )
            parentFragmentManager.popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCartProductRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val app = requireActivity().application as ShoppingApplication
        val selectedProducts =
            arguments?.getSerializableCompat<CartProducts>(KEY_SELECTED_PRODUCTS) ?: return
        viewModel =
            ViewModelProvider(
                this,
                CartProductRecommendViewModelFactory(
                    selectedProducts,
                    app.recentProductRepository,
                    app.getPagedProductsUseCase,
                    app.getPagedCartProductsUseCase,
                    app.addToCartUseCase,
                    app.updateCartQuantityUseCase,
                ),
            )[CartProductRecommendViewModel::class.java]
        initBindings()
        initObservers()
    }

    private fun initBindings() {
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        binding.rvRecommendedProducts.adapter = adapter

        binding.btnOrder.setOnClickListener {
            val intent =
                PaymentActivity.newIntent(requireContext(), viewModel.cartProducts.value)
            startActivity(intent)
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
        private const val KEY_SELECTED_PRODUCTS = "selectedProducts"

        fun newBundle(selectedProducts: CartProducts?): Bundle =
            Bundle().apply {
                putSerializable(KEY_SELECTED_PRODUCTS, selectedProducts)
            }
    }
}
