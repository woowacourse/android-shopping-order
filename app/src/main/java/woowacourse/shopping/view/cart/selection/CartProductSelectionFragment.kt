package woowacourse.shopping.view.cart.selection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.databinding.FragmentCartProductSelectionBinding
import woowacourse.shopping.view.cart.recommendation.CartProductRecommendationFragment
import woowacourse.shopping.view.cart.selection.adapter.CartProductAdapter

class CartProductSelectionFragment() : Fragment() {
    private val viewModel by lazy {
        val app = requireContext().applicationContext as ShoppingApplication
        ViewModelProvider(
            this,
            CartProductSelectionViewModelFactory(app.cartProductRepository),
        )[CartProductSelectionViewModel::class.java]
    }
    private var _binding: FragmentCartProductSelectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CartProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCartProductSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initBindings()
        initObservers()
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadPage()
    }

    private fun initRecyclerView() {
        adapter = CartProductAdapter(eventHandler = viewModel)
        binding.rvProducts.adapter = adapter
        binding.rvProducts.itemAnimator = null
    }

    private fun initBindings() {
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        binding.handler = viewModel
        binding.btnOrder.setOnClickListener {
            parentFragmentManager.commit {
                replace(
                    R.id.fragment,
                    CartProductRecommendationFragment::class.java,
                    CartProductRecommendationFragment.newBundle(
                        viewModel.selectedProducts,
                    ),
                )
            }
        }
    }

    private fun initObservers() {
        viewModel.products.observe(viewLifecycleOwner) { value ->
            adapter.submitList(value)
        }

        viewModel.isFinishedLoading.observe(viewLifecycleOwner) { value ->
            when (value) {
                true -> binding.sfLoading.visibility = View.GONE
                false -> {
                    binding.sfLoading.visibility = View.VISIBLE
                    binding.sfLoading.startShimmer()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
