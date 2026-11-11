package com.zyranto.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zyranto.app.BuildConfig
import com.zyranto.app.data.*
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {
    private val repo: ProductRepository =
        if (BuildConfig.DEMO_MODE) DemoRepository() else WooRepository(BuildConfig.STORE_URL)

    var products by mutableStateOf<List<Product>>(emptyList()); private set
    var loading by mutableStateOf(true); private set
    var error by mutableStateOf<String?>(null); private set
    val cart = mutableStateMapOf<Int, Int>()
    val wishlist = mutableStateListOf<Int>()

    init { refresh() }

    /** Re-fetches the catalogue. Silent refreshes keep the current list visible (auto-sync). */
    fun refresh(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) loading = true
            runCatching { repo.products() }
                .onSuccess { products = it; error = null; pruneCart() }
                .onFailure { if (!silent) error = it.message ?: "Could not load products" }
            loading = false
        }
    }

    private fun pruneCart() {
        cart.keys.toList().forEach { id ->
            val p = products.firstOrNull { it.id == id }
            if (p == null || !p.inStock) cart.remove(id)
        }
    }

    fun product(id: Int) = products.firstOrNull { it.id == id }
    val cartCount: Int get() = cart.values.sum()
    val cartTotal: Double get() = cart.entries.sumOf { (id, q) -> (product(id)?.price ?: 0.0) * q }

    fun add(p: Product) { if (p.inStock && (cart[p.id] ?: 0) < p.stock) cart[p.id] = (cart[p.id] ?: 0) + 1 }
    fun dec(p: Product) {
        val q = cart[p.id] ?: return
        if (q <= 1) cart.remove(p.id) else cart[p.id] = q - 1
    }
    fun remove(id: Int) { cart.remove(id) }
    fun toggleWish(id: Int) { if (!wishlist.remove(id)) wishlist.add(id) }

    suspend fun placeOrder(info: DeliveryInfo): Result<String> {
        val r = repo.placeOrder(cart.toMap(), info)
        if (r.isSuccess) cart.clear()
        return r
    }
}
