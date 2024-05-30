package woowacourse.shopping.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import woowacourse.shopping.databinding.FragmentCartSelectionBinding
import woowacourse.shopping.ui.cart.adapter.CartAdapter

class CartSelectionFragment(val viewModel: CartViewModel) : Fragment() {
    private lateinit var binding: FragmentCartSelectionBinding
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCartSelectionBinding.inflate(inflater, container, false)
        cartAdapter = CartAdapter(viewModel)
        val listView = binding.rvCart
        listView.adapter = cartAdapter
        return binding.root
    }
}
