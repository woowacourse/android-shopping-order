package woowacourse.shopping.feature.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import woowacourse.shopping.data.carts.CartFetchError
import woowacourse.shopping.data.carts.repository.CartRemoteDataSourceImpl
import woowacourse.shopping.data.carts.repository.CartRepositoryImpl
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.feature.QuantityChangeListener
import woowacourse.shopping.feature.cart.adapter.CartAdapter
import woowacourse.shopping.feature.cart.adapter.CartSkeletonAdapter
import woowacourse.shopping.feature.cart.adapter.CartViewHolder

class CartActivity :
    AppCompatActivity(),
    CartViewHolder.CartClickListener {
    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels {
        CartViewModelFactory(CartRepositoryImpl(CartRemoteDataSourceImpl()))
    }

    private val cartAdapter: CartAdapter by lazy {
        CartAdapter(
            this,
            quantityChangeListener =
                object : QuantityChangeListener {
                    override fun onIncrease(cartItem: CartItem) {
                        viewModel.increaseQuantity(cartItem)
                    }

                    override fun onDecrease(cartItem: CartItem) {
                        viewModel.removeCartItemOrDecreaseQuantity(cartItem)
                    }
                },
        )
    }

    private val cartSkeletonAdapter: CartSkeletonAdapter by lazy {
        CartSkeletonAdapter()
    }

    private val concatAdapter: ConcatAdapter by lazy {
        ConcatAdapter(cartSkeletonAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.rvCartItems.adapter = concatAdapter
        binding.viewModel = viewModel
        viewModel.loginErrorEvent.observe(this) { result ->
            when (result) {
                CartFetchError.Network -> Toast.makeText(this, "네트워크 에러 발생", Toast.LENGTH_SHORT).show()
                is CartFetchError.Server -> Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
        viewModel.removeItemEvent.observe(this) { cartItem ->
            onCartItemDelete(cartItem)
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (!isLoading) {
                concatAdapter.removeAdapter(cartSkeletonAdapter)
                concatAdapter.addAdapter(0, cartAdapter)
            } else {
                if (concatAdapter.adapters.contains(cartSkeletonAdapter).not()) {
                    concatAdapter.addAdapter(0, cartSkeletonAdapter)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateCartQuantity()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onCartItemDelete(cartItem: CartItem) {
        val deletedIndex: Int? = viewModel.getPosition(cartItem)
        deletedIndex?.let { cartAdapter.removeItem(it) }
        viewModel.delete(cartItem)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, CartActivity::class.java)
    }
}
