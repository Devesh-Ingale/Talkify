package dev.devlopment.chater.Screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.devlopment.chater.ViewModels.MessageViewModel
import dev.devlopment.chater.ViewModels.RoomViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    messageViewModel: MessageViewModel = viewModel(),
    roomViewModel: RoomViewModel = viewModel()
) {
    val tabItems = listOf("Classes", "Direct Messaging")
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { tabItems.size }
    )

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TabRow(
                selectedTabIndex = pagerState.currentPage
            ) {
                tabItems.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(index)
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(padding)
        ) { page ->
            when (page) {
                0 -> ClassesScreen(roomViewModel)
                1 -> DirectMessagesScreen(messageViewModel)
            }
        }
    }
}

@Composable
fun ClassesScreen(roomViewModel: RoomViewModel) {
    ChatRoomListScreen(roomViewModel = roomViewModel, onJoinClicked = {}, onAiClicked = {})
}

@Composable
fun DirectMessagesScreen(messageViewModel: MessageViewModel) {
    // Implement the UI for direct messaging
    // Similar to ChatScreen but tailored for one-to-one messaging
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}
