package woowacourse.shopping.view.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import woowacourse.shopping.databinding.FragmentCartProductSelectionBinding
import woowacourse.shopping.view.cart.adapter.CartProductAdapter

class CartProductSelectionFragment(
    private val viewModel: ShoppingCartViewModel,
) : Fragment() {
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

    private fun initRecyclerView() {
        adapter = CartProductAdapter(eventHandler = viewModel)
        binding.rvProducts.adapter = adapter
        binding.rvProducts.itemAnimator = null
    }

    private fun initBindings() {
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        binding.handler = viewModel
    }

    private fun initObservers() {
        viewModel.products.observe(viewLifecycleOwner) { value ->
            adapter.updateItems(value)
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
