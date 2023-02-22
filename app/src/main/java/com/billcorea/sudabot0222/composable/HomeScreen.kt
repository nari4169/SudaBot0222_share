package com.billcorea.sudabot0222.composable

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.billcorea.sudabot0222.retrofit.ChatData
import com.billcorea.sudabot0222.retrofit.DataViewModel
import com.billcorea.sudabot0222.ui.theme.softBlue
import com.billcorea.sudabot0222.ui.theme.typography
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import com.billcorea.sudabot0222.R

@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen (
    destinationsNavigator: DestinationsNavigator,
    dataViewModel: DataViewModel,
    modifier: Modifier
) {
    Log.e("", "list= ${dataViewModel.chatLists.size}")
    if (dataViewModel.chatLists.size == 0) {
        dataViewModel.chatLists.add(ChatData("me", "입력된 자료가 없습니다.", ""))
    }

    val listState = rememberLazyListState()
    // Remember a CoroutineScope to be able to launch
    val coroutineScope = rememberCoroutineScope()

    Column( modifier = Modifier
        .fillMaxSize()
        .padding(3.dp)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed( dataViewModel.chatLists ) {index, item ->
                DisplayItem(item)
                if (index % 5 == 0) {
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp),
                        border = BorderStroke(1.dp, softBlue)
                    ) {
                        AndroidView(
                            modifier = Modifier.fillMaxWidth(),
                            factory = { context ->
                                AdView(context).apply {
                                    this.setAdSize(AdSize.BANNER)
                                    adUnitId = context.getString(R.string.ADMOB_BANNER_ID)
                                    loadAd(AdRequest.Builder().build())
                                }
                            }
                        )
                    }
                }
            }
            coroutineScope.launch {
                // Animate scroll to the first item
                if (dataViewModel.chatLists.size > 0) {
                    listState.animateScrollToItem(index = dataViewModel.chatLists.size)
                } else {
                    listState.animateScrollToItem(index = 0)
                }
            }
        }
    }
}

@Composable
fun DisplayItem(item: ChatData) {
    var horizontalPosition = Arrangement.Start
    if (item.id == "me") {
        horizontalPosition = Arrangement.End
    }
    Log.e("", "chatMessage= ${item.chatMessage} ${item.id}")
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(3.dp),
        border = BorderStroke(1.dp, softBlue)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = horizontalPosition
        ) {
            if (item.id == "me") {
                Text(text = item.chatMessage, style = typography.bodyLarge)
            } else {
                Text(text = item.convMessage, style = typography.bodyLarge)
            }
        }
    }
}
