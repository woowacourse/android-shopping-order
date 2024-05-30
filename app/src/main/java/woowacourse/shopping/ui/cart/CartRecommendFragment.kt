package woowacourse.shopping.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartRecommendBinding

class CartRecommendFragment(val viewModel: CartViewModel) : Fragment() {
    private lateinit var binding: FragmentCartRecommendBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCartRecommendBinding.inflate(inflater, container, false)
        val listView = binding.rvRecommendProduct
        return inflater.inflate(R.layout.fragment_cart_recommend, container, false)
    }
}
