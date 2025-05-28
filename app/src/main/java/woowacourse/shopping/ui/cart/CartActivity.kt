package woowacourse.shopping.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.fragment.app.commit
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.ui.common.DataBindingActivity
import woowacourse.shopping.ui.model.ActivityResult

class CartActivity : DataBindingActivity<ActivityCartBinding>(R.layout.activity_cart) {
    private val viewModel: CartViewModel by viewModels { CartViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
            }
        }

        initSupportActionBar()
        initViewBinding()
        initObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun initSupportActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.cart_title)
    }

    private fun initViewBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun initObservers() {
        viewModel.editedProductIds.observe(this) { editedProductIds ->
            setResult(
                ActivityResult.CART_PRODUCT_EDITED.code,
                Intent().apply {
                    putExtra(
                        ActivityResult.CART_PRODUCT_EDITED.key,
                        editedProductIds.toLongArray(),
                    )
                },
            )
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, CartActivity::class.java)
    }
}
