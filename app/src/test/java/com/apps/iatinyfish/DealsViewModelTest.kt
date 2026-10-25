package com.apps.iatinyfish

import com.apps.iatinyfish.data.model.ProductOffer
import com.apps.iatinyfish.data.repository.DealsRepository
import com.apps.iatinyfish.presentation.deals.DealsEvent
import com.apps.iatinyfish.presentation.deals.DealsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DealsViewModelTest {
    private val fake = object : DealsRepository {
        override val isPreviewData = true
        override suspend fun search(query: String) = listOf(
            ProductOffer("1", "Tienda", query, "\$10", null, "Gratis", "1 día", "5", "https://x.com", "ok", emptyList()),
        )
    }

    @Before fun setUp() = Dispatchers.setMain(StandardTestDispatcher())
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun blankSearchShowsError() = runTest {
        val vm = DealsViewModel(fake)
        vm.onEvent(DealsEvent.SubmitSearch)
        assertNotNull(vm.state.value.errorMessage)
        assertTrue(vm.state.value.offers.isEmpty())
    }

    @Test fun searchReturnsOffers() = runTest {
        val vm = DealsViewModel(fake)
        vm.onEvent(DealsEvent.UseSuggestion("switch"))
        advanceUntilIdle()
        assertEquals(1, vm.state.value.offers.size)
        assertEquals(false, vm.state.value.isLoading)
    }
}
