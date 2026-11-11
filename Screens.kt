package com.zyranto.app.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zyranto.app.data.Categories
import com.zyranto.app.data.Config
import com.zyranto.app.data.DeliveryInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ---------------- HOME ----------------
@Composable
fun Banner(onShopNow: () -> Unit) {
    val slides = listOf(
        Triple("Shop Smart,", "Live Better", "Quality products, great value, all in one place."),
        Triple("Fresh Fashion,", "New Arrivals", "Style that fits your everyday life."),
        Triple("Smart Gadgets,", "Great Prices", "Genuine products delivered across Bangladesh.")
    )
    val state = rememberPagerState { slides.size }
    LaunchedEffect(Unit) { while (true) { delay(4000); state.animateScrollToPage((state.currentPage + 1) % slides.size) } }
    Box(
        Modifier.fillMaxWidth().height(240.dp)
            .background(Brush.horizontalGradient(listOf(Ink, Color(0xFF2A210B), Color(0xFF7A5A12))))
    ) {
        HorizontalPager(state) { i ->
            Column(Modifier.fillMaxSize().padding(horizontal = 24.dp), verticalArrangement = Arrangement.Center) {
                Text("WELCOME TO ZYRANTO", color = Gold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(slides[i].first, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp)
                Text(slides[i].second, color = Gold, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp)
                Spacer(Modifier.height(4.dp))
                Text(slides[i].third, color = Color.White, fontSize = 14.sp)
                Spacer(Modifier.height(14.dp))
                GoldButton("Shop Now →", onShopNow)
            }
        }
        Row(Modifier.align(Alignment.BottomCenter).padding(10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(slides.size) { i ->
                Box(Modifier.height(8.dp).width(if (i == state.currentPage) 24.dp else 8.dp)
                    .background(if (i == state.currentPage) Gold else Color.White, CircleShape))
            }
        }
    }
}

@Composable
fun HomeScreen(vm: AppViewModel, onOpen: (Int) -> Unit, onShop: (String?) -> Unit, onAllCats: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize()) {
        item { Banner { onShop(null) } }
        item {
            Row(Modifier.fillMaxWidth().padding(end = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                SectionTitle("Shop by Category", Modifier.weight(1f))
                TextButton(onClick = onAllCats) { Text("All →", color = Ink, fontWeight = FontWeight.Bold) }
            }
        }
        item {
            LazyRow(contentPadding = PaddingValues(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(Categories.all) { c ->
                    Card(
                        onClick = { onShop(c.name) }, Modifier.width(100.dp), shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(10.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(c.icon, null, Modifier.size(34.dp))
                            Spacer(Modifier.height(6.dp))
                            Text(c.name, fontSize = 12.sp, textAlign = TextAlign.Center, maxLines = 2, minLines = 2)
                        }
                    }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth().padding(end = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                SectionTitle("Our Featured Products", Modifier.weight(1f))
                TextButton(onClick = { onShop(null) }) { Text("View All →", color = Ink, fontWeight = FontWeight.Bold) }
            }
        }
        when {
            vm.loading -> item { Box(Modifier.fillMaxWidth().padding(24.dp), Alignment.Center) { CircularProgressIndicator(color = Gold) } }
            vm.error != null -> item {
                Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(vm.error ?: "", color = Color.Red, textAlign = TextAlign.Center)
                    GoldButton("Retry", { vm.refresh() })
                }
            }
            else -> productRows(vm.products.take(4), vm, onOpen)
        }
        item { TrustStrip() }
        item { Footer() }
    }
}

@Composable
fun TrustStrip() {
    val items = listOf(
        Icons.Filled.VerifiedUser to "100% Genuine", Icons.Filled.LocalShipping to "Fast Delivery",
        Icons.Filled.SupportAgent to "24/7 Support", Icons.Filled.Autorenew to "Easy Return"
    )
    Row(Modifier.fillMaxWidth().padding(12.dp).background(Color.White, RoundedCornerShape(10.dp)).padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly) {
        items.forEach { (ic, t) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(ic, null, Modifier.size(26.dp)); Text(t, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun Footer() {
    Column(Modifier.fillMaxWidth().background(Ink).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("ZYRANTO", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
        Text("Your Everyday Shopping Destination", color = Gold, fontSize = 11.sp)
        Spacer(Modifier.height(8.dp))
        Text("© 2025 ZYRANTO. All rights reserved.", color = Color.Gray, fontSize = 11.sp)
    }
}

// ---------------- SHOP / CATEGORIES / SEARCH ----------------
@Composable
private fun Chip(label: String, selected: Boolean, onClick: () -> Unit) = FilterChip(
    selected, onClick, label = { Text(label) },
    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Gold, selectedLabelColor = Ink)
)

@Composable
fun ShopScreen(vm: AppViewModel, initialCat: String?, onOpen: (Int) -> Unit) {
    var cat by remember { mutableStateOf(initialCat) }
    val list = vm.products.filter { cat == null || it.category == cat }
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            LazyRow(contentPadding = PaddingValues(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Chip("All", cat == null) { cat = null } }
                items(Categories.all) { c -> Chip(c.name, cat == c.name) { cat = c.name } }
            }
        }
        if (list.isEmpty() && !vm.loading) item { Text("No products found.", Modifier.padding(24.dp)) }
        productRows(list, vm, onOpen)
    }
}

@Composable
fun CategoriesScreen(onShop: (String?) -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(Categories.all) { c ->
            Card(onClick = { onShop(c.name) }, Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(c.icon, null, Modifier.size(32.dp)); Spacer(Modifier.width(16.dp))
                    Text(c.name, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Icon(Icons.Filled.ChevronRight, null)
                }
            }
        }
    }
}

@Composable
fun SearchScreen(vm: AppViewModel, onOpen: (Int) -> Unit) {
    var q by remember { mutableStateOf("") }
    val res = if (q.isBlank()) emptyList() else vm.products.filter {
        it.name.contains(q, true) || it.sku.contains(q, true) || it.category.contains(q, true)
    }
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            OutlinedTextField(q, { q = it }, Modifier.fillMaxWidth().padding(12.dp), singleLine = true,
                placeholder = { Text("Search products...") }, leadingIcon = { Icon(Icons.Filled.Search, null) })
        }
        if (q.isNotBlank() && res.isEmpty()) item { Text("No results for \"$q\"", Modifier.padding(16.dp)) }
        productRows(res, vm, onOpen)
    }
}

// ---------------- DETAIL ----------------
@Composable
fun DetailScreen(vm: AppViewModel, id: Int) {
    val p = vm.product(id)
    if (p == null) { Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Product not found") }; return }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ProductImage(p, Modifier.fillMaxWidth().height(280.dp))
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(p.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(money(p.price), color = GoldDark, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                if (p.onSale) { Spacer(Modifier.width(8.dp)); Text(money(p.regularPrice), color = Color.Gray) }
            }
            Text(if (p.inStock) "In stock" else "Out of stock", color = if (p.inStock) Color(0xFF2E7D32) else Color.Red, fontWeight = FontWeight.SemiBold)
            Text("SKU: ${p.sku}  •  ${p.category}", fontSize = 12.sp, color = Color.Gray)
            Text(p.description.ifBlank { "No description available." })
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GoldButton(if (p.inStock) "Add to Cart" else "Out of Stock", { vm.add(p) }, Modifier.weight(1f), enabled = p.inStock)
                OutlinedButton({ vm.toggleWish(p.id) }) {
                    Icon(if (p.id in vm.wishlist) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, "Wishlist")
                }
            }
        }
    }
}

// ---------------- CART / WISHLIST ----------------
@Composable
fun CartScreen(vm: AppViewModel, onCheckout: () -> Unit, onShop: () -> Unit) {
    val lines = vm.cart.keys.toList().mapNotNull { vm.product(it) }
    if (lines.isEmpty()) {
        Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
            Icon(Icons.Filled.ShoppingCart, null, Modifier.size(64.dp), tint = Gold)
            Text("Your cart is empty", Modifier.padding(12.dp))
            GoldButton("Start Shopping", onShop)
        }
        return
    }
    Column(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(lines) { p ->
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        ProductImage(p, Modifier.size(64.dp))
                        Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                            Text(p.name, fontWeight = FontWeight.SemiBold, maxLines = 1)
                            Text(money(p.price), color = GoldDark, fontWeight = FontWeight.Bold)
                        }
                        IconButton({ vm.dec(p) }) { Icon(Icons.Filled.Remove, "Less") }
                        Text("${vm.cart[p.id] ?: 0}", fontWeight = FontWeight.Bold)
                        IconButton({ vm.add(p) }) { Icon(Icons.Filled.Add, "More") }
                        IconButton({ vm.remove(p.id) }) { Icon(Icons.Filled.Delete, "Remove", tint = Color.Red) }
                    }
                }
            }
        }
        Column(Modifier.background(Color.White).padding(16.dp)) {
            Row { Text("Total", Modifier.weight(1f), fontWeight = FontWeight.Bold); Text(money(vm.cartTotal), fontWeight = FontWeight.Bold, color = GoldDark) }
            Spacer(Modifier.height(10.dp))
            GoldButton("Proceed to Checkout", onCheckout, Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun WishlistScreen(vm: AppViewModel, onOpen: (Int) -> Unit) {
    val list = vm.products.filter { it.id in vm.wishlist }
    if (list.isEmpty()) { Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Your wishlist is empty") }; return }
    LazyColumn(Modifier.fillMaxSize()) { productRows(list, vm, onOpen) }
}

// ---------------- CHECKOUT ----------------
@Composable
fun CheckoutScreen(vm: AppViewModel, onDone: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Delivery Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("Full name") }, singleLine = true)
        OutlinedTextField(phone, { phone = it }, Modifier.fillMaxWidth(), label = { Text("Mobile (01XXXXXXXXX)") }, singleLine = true)
        OutlinedTextField(address, { address = it }, Modifier.fillMaxWidth(), label = { Text("Full address") })
        OutlinedTextField(city, { city = it }, Modifier.fillMaxWidth(), label = { Text("City / District") }, singleLine = true)
        Text("Payment", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) { RadioButton(true, {}); Text("Cash on Delivery") }
        Row(verticalAlignment = Alignment.CenterVertically) { RadioButton(false, {}, enabled = false); Text("bKash / Nagad (coming soon)", color = Color.Gray) }
        Text("Total: ${money(vm.cartTotal)}", fontWeight = FontWeight.Bold, color = GoldDark)
        GoldButton(if (busy) "Placing..." else "Place Order", {
            when {
                vm.cart.isEmpty() -> msg = "Your cart is empty."
                name.isBlank() || address.isBlank() || city.isBlank() -> msg = "Please fill in all delivery details."
                !Regex("^01\\d{9}$").matches(phone.trim()) -> msg = "Enter a valid 11-digit mobile number."
                else -> scope.launch {
                    busy = true
                    val r = vm.placeOrder(DeliveryInfo(name.trim(), phone.trim(), address.trim(), city.trim()))
                    busy = false
                    if (r.isSuccess) onDone() else msg = r.exceptionOrNull()?.message
                }
            }
        }, Modifier.fillMaxWidth(), enabled = !busy)
        msg?.let { Text(it, color = Color.Red) }
    }
}

// ---------------- ACCOUNT / LOGIN / ORDERS / CONTACT ----------------
@Composable
fun AccountScreen(go: (String) -> Unit) {
    val rows: List<Triple<ImageVector, String, String>> = listOf(
        Triple(Icons.Filled.Login, "Login / Register", "login"),
        Triple(Icons.Filled.ReceiptLong, "My Orders", "orders"),
        Triple(Icons.Filled.Favorite, "Wishlist", "wishlist"),
        Triple(Icons.Filled.Category, "Categories", "categories"),
        Triple(Icons.Filled.Chat, "Contact Us", "contact")
    )
    Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Welcome, Guest", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        rows.forEach { (ic, t, r) ->
            Card(onClick = { go(r) }, Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(ic, null, tint = GoldDark); Spacer(Modifier.width(14.dp))
                    Text(t, Modifier.weight(1f)); Icon(Icons.Filled.ChevronRight, null)
                }
            }
        }
    }
}

@Composable
fun LoginScreen() {
    var register by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Chip("Login", !register) { register = false }
            Chip("Register", register) { register = true }
        }
        if (register) OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("Full name") }, singleLine = true)
        OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(), label = { Text("Email") }, singleLine = true)
        OutlinedTextField(pass, { pass = it }, Modifier.fillMaxWidth(), label = { Text("Password") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation())
        GoldButton(if (register) "Create Account" else "Login", {
            msg = "Customer accounts need the secure backend (Stage 4). Not connected yet."
        }, Modifier.fillMaxWidth())
        msg?.let { Text(it, color = Color.Red) }
    }
}

@Composable
fun OrdersScreen() {
    Column(Modifier.fillMaxSize().padding(24.dp), Arrangement.Center, Alignment.CenterHorizontally) {
        Icon(Icons.Filled.ReceiptLong, null, Modifier.size(64.dp), tint = Gold)
        Text("No orders yet", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Your orders and their live status will appear here once the store backend is connected.", textAlign = TextAlign.Center, color = Color.Gray)
    }
}

@Composable
fun ContactScreen() {
    val ctx = LocalContext.current
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Contact Us", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("We're here to help. 24/7 customer support.")
        GoldButton("Order via WhatsApp", {
            ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${Config.WHATSAPP_NUMBER}")))
        }, Modifier.fillMaxWidth())
        OutlinedButton({
            ctx.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${Config.SUPPORT_EMAIL}")))
        }, Modifier.fillMaxWidth()) { Icon(Icons.Filled.Email, null); Spacer(Modifier.width(8.dp)); Text("Email Us") }
    }
}
