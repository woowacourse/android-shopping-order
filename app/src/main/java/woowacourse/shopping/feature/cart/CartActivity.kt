package woowacourse.shopping.feature.cart

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.application.ShoppingApplication
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.feature.cart.adapter.CartAdapter
import woowacourse.shopping.feature.cart.adapter.CartViewHolder
import woowacourse.shopping.feature.model.ResultCode

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels {
        (application as ShoppingApplication).cartFactory
    }
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAdapter()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.rvGoods.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

//    private fun updatePageButton() {
//        viewModel.totalItemsCount.observe(this) {
// //            viewModel.updatePageButtonStates()
//        }
//        viewModel.page.observe(this) {
// //            viewModel.updatePageButtonStates()
//        }
//    }

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
        setResult(
            ResultCode.CART_INSERT.code,
            Intent().apply {
                putExtra("GOODS_ID", cart.product.id)
                putExtra("GOODS_QUANTITY", quantity)
            },
        )
    }
}
