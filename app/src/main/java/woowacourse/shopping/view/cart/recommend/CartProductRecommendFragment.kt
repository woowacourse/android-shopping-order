package woowacourse.shopping.view.cart.recommend

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.databinding.FragmentCartProductRecommendBinding
import woowacourse.shopping.domain.model.CartProduct
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
            parentFragmentManager.commit {
                replace(
                    this@CartProductRecommendFragment.id,
                    CartProductSelectFragment::class.java,
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
        _binding = FragmentCartProductRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val selectedProducts =
            arguments
                ?.getSerializableCompat<ArrayList<CartProduct>>(KEY_SELECTED_PRODUCTS)
                .orEmpty()
                .toSet()
        val app = requireActivity().application as ShoppingApplication
        viewModel =
            ViewModelProvider(
                this,
                CartProductRecommendViewModelFactory(
                    selectedProducts,
                    app.productRepository,
                    app.cartProductRepository,
                    app.recentProductRepository,
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
            val intent = PaymentActivity.newIntent(requireContext())
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

        fun newBundle(selectedProducts: Set<CartProduct>): Bundle =
            Bundle().apply {
                putSerializable(KEY_SELECTED_PRODUCTS, ArrayList(selectedProducts))
            }
    }
}
