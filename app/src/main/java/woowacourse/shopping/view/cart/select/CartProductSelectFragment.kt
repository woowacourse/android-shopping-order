package woowacourse.shopping.view.cart.select

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartProductSelectBinding
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.view.cart.recommend.CartProductRecommendFragment
import woowacourse.shopping.view.cart.select.adapter.CartProductAdapter

class CartProductSelectFragment(
    repository: CartProductRepository,
) : Fragment() {
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            CartProductSelectViewModelFactory(repository),
        )[CartProductSelectViewModel::class.java]
    }
    private var _binding: FragmentCartProductSelectBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CartProductAdapter

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
    }

    private fun initBindings() {
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        binding.handler = viewModel
        binding.btnOrder.setOnClickListener {
            parentFragmentManager.commit {
                replace(
                    R.id.fragment,
                    CartProductRecommendFragment::class.java,
                    CartProductRecommendFragment.newBundle(
                        viewModel.selectedIds,
                        viewModel.totalPrice.value,
                        viewModel.totalCount.value,
                    ),
                )
            }
        }
    }

    private fun initObservers() {
        viewModel.products.observe(viewLifecycleOwner) { value ->
            adapter.submitList(value)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
