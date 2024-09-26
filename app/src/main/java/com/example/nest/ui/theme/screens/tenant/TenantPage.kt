package com.example.nest.ui.theme.screens.tenant

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.nest.R
import com.example.nest.data.LandlordViewModel
import com.example.nest.navigation.ROUTE_VIEW_APARTMENTS
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Composable
fun TenantScreen(navController: NavController) {
    var location by remember { mutableStateOf("") }
    var apartments by remember { mutableStateOf<List<LandlordViewModel>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.stairs),
            contentDescription = "Tenant background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Welcome to Your New Home Search!",
                color = Color.Black,
                textDecoration = TextDecoration.Underline,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(top = 32.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                color = Color.Black,
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                text = "Input your desired location!"
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { newLocation -> location = newLocation },
                label = { Text("Location") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    loading = true
                    coroutineScope.launch {
                        apartments = searchApartments(location)
                        loading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(Color.DarkGray)
            ) {
                Text(
                    text = "SEARCH",
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }

            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

//            apartments.forEach { apartment ->
////               Text(text = "Apartment: ${apartment.name}"
//            //               "Location: ${apartment.location}"
//            //               "Price: ${apartment.price}"
//            //               "Description: ${apartment.description}"
//
//            //               )
            }
        }
    }



suspend fun searchApartments(location: String): List<LandlordViewModel> {
    val apartmentsList = mutableListOf<LandlordViewModel>()
    val db = Firebase.firestore

    try {
        val result = Tasks.await(db.collection("apartments")
            .whereEqualTo("location", location)
            .get())

        for (document in result) {
            val apartment = document.toObject(LandlordViewModel::class.java)
            apartmentsList.add(apartment)
        }
    } catch (exception: Exception) {
        Log.w("Firestore", "Error getting documents: ", exception)
    }

    return apartmentsList
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun TenantScreenPreview() {
    TenantScreen(rememberNavController())
}
