package woowacourse.shopping.feature.cart

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartProductBinding
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.feature.cart.adapter.CartAdapter
import woowacourse.shopping.feature.cart.adapter.CartViewHolder
import woowacourse.shopping.feature.model.ResultCode

class CartProductFragment : Fragment() {
    private lateinit var binding: FragmentCartProductBinding
    private val viewModel: CartViewModel by activityViewModels<CartViewModel>()
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart_product, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setupAdapter()
        binding.rvGoods.adapter = adapter
        return binding.root
    }

    private fun setupAdapter() {
        adapter =
            CartAdapter(
                object : CartViewHolder.CartClickListener {
                    override fun onClickDeleteButton(cart: Cart) {
                        viewModel.delete(cart)
                        sendCartResult(cart, 0)
                    }

                    override fun addToCart(cart: Cart) {
                        viewModel.addToCart(cart)
                        sendCartResult(cart, cart.quantity + 1)
                    }

                    override fun removeFromCart(cart: Cart) {
                        viewModel.removeFromCart(cart)
                        sendCartResult(cart, cart.quantity - 1)
                    }

                    override fun toggleCheckedItem(cart: Cart) {
                        viewModel.toggleCheck(cart)
                    }
                },
            )
    }

    private fun sendCartResult(
        cart: Cart,
        quantity: Int,
    ) {
        requireActivity().setResult(
            ResultCode.CART_INSERT.code,
            Intent().apply {
                putExtra("GOODS_ID", cart.product.id)
                putExtra("GOODS_QUANTITY", quantity)
            },
        )
    }
}
