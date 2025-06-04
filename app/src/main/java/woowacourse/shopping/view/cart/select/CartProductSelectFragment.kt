package woowacourse.shopping.view.cart.select

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.databinding.FragmentCartProductSelectBinding
import woowacourse.shopping.view.cart.recommend.CartProductRecommendFragment
import woowacourse.shopping.view.cart.select.adapter.CartProductAdapter

class CartProductSelectFragment(
    application: ShoppingApplication,
) : Fragment() {
    private var _binding: FragmentCartProductSelectBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            CartProductSelectViewModelFactory(application.cartProductRepository),
        )[CartProductSelectViewModel::class.java]
    }

    private val adapter: CartProductAdapter by lazy {
        CartProductAdapter(eventHandler = viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCartProductSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initBindings()
        initObservers()
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadPage()
    }

    private fun initBindings() {
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        binding.handler = viewModel
        binding.rvProducts.adapter = adapter

        binding.btnOrder.setOnClickListener {
            parentFragmentManager.commit {
                replace(
                    R.id.fragment,
                    CartProductRecommendFragment::class.java,
                    CartProductRecommendFragment.newBundle(
                        viewModel.selectedCartProducts.value
                            .orEmpty()
                            .map { it.id }
                            .toSet(),
                        viewModel.totalPrice.value,
                        viewModel.totalCount.value,
                    ),
                )
            }
        }
    }

    private fun initObservers() {
        viewModel.cartProductItems.observe(viewLifecycleOwner) { value ->
            adapter.submitList(value)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
