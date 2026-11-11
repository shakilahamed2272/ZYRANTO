package com.zyranto.app.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.zyranto.app.data.Product
import java.util.Locale

fun money(v: Double) = "৳" + String.format(Locale.US, "%,.0f", v)

@Composable
fun GoldButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick, modifier = modifier, enabled = enabled, shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)
    ) { Text(text, fontWeight = FontWeight.Bold) }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f).height(2.dp).background(Gold))
    }
}

@Composable
fun ProductImage(p: Product, modifier: Modifier = Modifier) {
    Box(modifier.background(Color(0xFFF1F1F1)), contentAlignment = Alignment.Center) {
        if (p.imageUrl != null)
            AsyncImage(p.imageUrl, p.name, Modifier.fillMaxSize().padding(8.dp), contentScale = ContentScale.Fit)
        else Icon(Icons.Filled.ShoppingBag, null, Modifier.size(48.dp), tint = Gold)
    }
}

@Composable
fun ProductCard(
    p: Product, wished: Boolean, onOpen: () -> Unit, onAdd: () -> Unit, onWish: () -> Unit, modifier: Modifier = Modifier
) {
    Card(
        modifier.clickable(onClick = onOpen), shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box {
                ProductImage(p, Modifier.fillMaxWidth().height(120.dp))
                IconButton(onWish, Modifier.align(Alignment.TopStart).size(36.dp)) {
                    Icon(if (wished) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, "Wishlist",
                        tint = if (wished) Color.Red else Ink, modifier = Modifier.size(20.dp))
                }
                if (p.onSale) Text("Sale", Modifier.align(Alignment.TopEnd).padding(6.dp)
                    .background(Gold, RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 2.dp),
                    fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Column(Modifier.padding(10.dp)) {
                Text(p.name, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(money(p.price), color = GoldDark, fontWeight = FontWeight.Bold)
                    if (p.onSale) {
                        Spacer(Modifier.width(6.dp))
                        Text(money(p.regularPrice), color = Color.Gray, fontSize = 12.sp, textDecoration = TextDecoration.LineThrough)
                    }
                }
                Spacer(Modifier.height(6.dp))
                Button(
                    onClick = onAdd, enabled = p.inStock, modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = RoundedCornerShape(50), contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Ink, contentColor = Color.White)
                ) {
                    Icon(Icons.Filled.ShoppingCart, null, Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (p.inStock) "Add to Cart" else "Out of Stock", fontSize = 12.sp)
                }
            }
        }
    }
}

fun LazyListScope.productRows(list: List<Product>, vm: AppViewModel, onOpen: (Int) -> Unit) {
    items(list.chunked(2)) { row ->
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            row.forEach { p ->
                ProductCard(p, p.id in vm.wishlist, { onOpen(p.id) }, { vm.add(p) }, { vm.toggleWish(p.id) }, Modifier.weight(1f))
            }
            if (row.size == 1) Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun ZyTopBar(cartCount: Int, onSearch: () -> Unit, onCart: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(Ink).statusBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.ShoppingBag, null, tint = Gold, modifier = Modifier.size(34.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text("ZYRANTO", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text("Shop Smart • Live Better", color = Gold, fontSize = 10.sp)
        }
        Spacer(Modifier.width(10.dp))
        Row(
            Modifier.weight(1f).height(38.dp).clip(RoundedCornerShape(50)).background(Color.White).clickable(onClick = onSearch)
                .padding(start = 14.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Search products...", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Box(Modifier.fillMaxHeight().width(40.dp).background(Gold), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Search, "Search", tint = Ink)
            }
        }
        IconButton(onCart) {
            BadgedBox(badge = { if (cartCount > 0) Badge { Text("$cartCount") } }) {
                Icon(Icons.Filled.ShoppingCart, "Cart", tint = Color.White)
            }
        }
    }
}
