package woowacourse.shopping.feature.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.carts.repository.CartRemoteDataSourceImpl
import woowacourse.shopping.data.carts.repository.CartRepositoryImpl
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.feature.QuantityChangeListener
import woowacourse.shopping.feature.cart.adapter.CartAdapter
import woowacourse.shopping.feature.cart.adapter.CartViewHolder

class CartActivity :
    AppCompatActivity(),
    CartViewHolder.CartClickListener {
    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels {
        CartViewModelFactory(CartRepositoryImpl(CartRemoteDataSourceImpl()))
    }
    private val adapter: CartAdapter by lazy {
        CartAdapter(
            this,
            quantityChangeListener =
                object : QuantityChangeListener {
                    override fun onIncrease(cartItem: CartItem) {
                        viewModel.addCartItemOrIncreaseQuantity(cartItem.copy(quantity = QUANTITY_UPDATE_UNIT))
                    }

                    override fun onDecrease(cartItem: CartItem) {
                        viewModel.removeCartItemOrDecreaseQuantity(cartItem.copy(quantity = QUANTITY_UPDATE_UNIT))
                    }
                },
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // showLoginDialog(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.rvCartItems.adapter = adapter
        binding.viewModel = viewModel
    }

    fun showLoginDialog(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_login, null)
        val idEditText = view.findViewById<EditText>(R.id.et_user_id)
        val pwEditText = view.findViewById<EditText>(R.id.et_password)

        AlertDialog
            .Builder(context)
            .setTitle("로그인")
            .setView(view)
            .setPositiveButton("로그인") { dialog, _ ->
                val id = idEditText.text.toString()
                val pw = pwEditText.text.toString()
                if (id.isNotBlank() && pw.isNotBlank()) {
                    viewModel.onLoginInput(id, pw)
                } else {
                    Toast.makeText(context, "아이디/비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                }
            }.setNegativeButton("취소", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateCartQuantity()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onClickDeleteButton(cartItem: CartItem) {
        val deletedIndex: Int? = viewModel.getPosition(cartItem)
        deletedIndex?.let { adapter.removeItem(it) }
        viewModel.delete(cartItem)
    }

    companion object {
        private const val QUANTITY_UPDATE_UNIT = 1

        fun newIntent(context: Context): Intent = Intent(context, CartActivity::class.java)
    }
}
