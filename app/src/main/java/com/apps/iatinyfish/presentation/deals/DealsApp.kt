package com.apps.iatinyfish.presentation.deals

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.apps.iatinyfish.data.model.ProductOffer

private val suggestions = listOf("Nintendo Switch OLED", "Audífonos Bluetooth", "Laptop gamer")

@Composable
fun DealsApp(state: DealsUiState, onEvent: (DealsEvent) -> Unit) {
    BackHandler(enabled = state.selectedOffer != null) { onEvent(DealsEvent.Back) }
    AnimatedContent(
        targetState = state.selectedOffer,
        transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(120)) },
        label = "screen",
    ) { offer ->
        if (offer == null) DealsSearchScreen(state, onEvent)
        else DealDetailScreen(offer, onBack = { onEvent(DealsEvent.Back) })
    }
}

@Composable
private fun DealsSearchScreen(state: DealsUiState, onEvent: (DealsEvent) -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                Modifier.fillMaxWidth().statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FishMark()
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("TinyPrice", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                        Text("Búsqueda web inteligente", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.query,
                    onValueChange = { onEvent(DealsEvent.QueryChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("¿Qué quieres comparar?") },
                    leadingIcon = {
                        Text(
                            "⌕",
                            fontSize = 26.sp,
                            modifier = Modifier.semantics { contentDescription = "Buscar productos" },
                        )
                    },
                    trailingIcon = {
                        TextButton(onClick = { onEvent(DealsEvent.SubmitSearch) }) {
                            Text("Buscar", fontWeight = FontWeight.Bold)
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onEvent(DealsEvent.SubmitSearch) }),
                    shape = RoundedCornerShape(18.dp),
                )
            }
        },
    ) { padding ->
        if (state.hasSearched) {
            SearchResults(state, padding.calculateTopPadding(), onEvent)
        } else {
            DiscoveryHome(padding.calculateTopPadding()) { onEvent(DealsEvent.UseSuggestion(it)) }
        }
    }
}

@Composable
private fun FishMark() {
    Box(
        Modifier.size(42.dp).background(MaterialTheme.colorScheme.primary, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text("◒", color = MaterialTheme.colorScheme.onPrimary, fontSize = 22.sp)
    }
}

@Composable
private fun DiscoveryHome(topPadding: Dp, onSuggestion: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp, topPadding + 12.dp, 20.dp, 20.dp)) {
        Text("Encuentra la mejor compra.", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(8.dp))
        Text("Compara fuentes sin abrir diez pestañas.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        Text("Prueba con:", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        suggestions.forEach { s ->
            AssistChip(
                onClick = { onSuggestion(s) },
                label = { Text(s) },
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun SearchResults(state: DealsUiState, topPadding: Dp, onEvent: (DealsEvent) -> Unit) {
    when {
        state.isLoading -> LoadingState(topPadding)
        state.offers.isEmpty() -> EmptyState(
            message = state.errorMessage ?: "No encontramos ofertas para “${state.query}”",
            topPadding = topPadding,
            onRetry = { onEvent(DealsEvent.SubmitSearch) },
        )
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp, topPadding + 6.dp, 20.dp, 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column {
                    Text(
                        "Resultados para “${state.query}”",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        if (state.isPreviewData) "Resultados de demostración"
                        else "Resultados encontrados en la web",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            items(state.offers, key = ProductOffer::id) { offer ->
                OfferCard(offer) { onEvent(DealsEvent.OpenOffer(offer)) }
            }
        }
    }
}

@Composable
private fun LoadingState(topPadding: Dp) {
    Column(
        Modifier.fillMaxSize().padding(top = topPadding)
            .semantics { liveRegion = LiveRegionMode.Polite },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
        Text("Buscando ofertas…", fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EmptyState(message: String, topPadding: Dp, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(top = topPadding).padding(24.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(message, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Reintentar") }
    }
}

@Composable
private fun OfferCard(offer: ProductOffer, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(offer.store, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(offer.productName, style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.width(8.dp))
                Text(offer.price, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(offer.shipping, style = MaterialTheme.typography.bodySmall)
                Text("★ ${offer.rating}", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DealDetailScreen(offer: ProductOffer, onBack: () -> Unit) {
    val context = LocalContext.current
    val canOpen = offer.sourceUrl.startsWith("http://") || offer.sourceUrl.startsWith("https://")
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            TextButton(onClick = onBack) { Text("← Volver") }
            Spacer(Modifier.height(8.dp))
            Text(offer.store, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text(offer.productName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(8.dp))
            Text(offer.price, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(16.dp))
            Text(offer.summary)
            Spacer(Modifier.height(16.dp))
            Text("Entrega: ${offer.delivery}")
            Text("Envío: ${offer.shipping}")
            Text("Valoración: ★ ${offer.rating}")
            Text("Fuente: ${offer.sourceUrl}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            if (canOpen) {
                Button(
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, offer.sourceUrl.toUri()))
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Abrir fuente web") }
            }
        }
    }
}
