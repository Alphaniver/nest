package com.example.nest.ui.theme.screens.landlord


import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.nest.data.ApartmentViewModel
import com.example.nest.models.Apartment
import com.example.nest.navigation.ROUTE_EDIT


@Composable
fun ViewApartmentsScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        val apartmentRepository = ApartmentViewModel(navController, context)

        var emptyUploadState = remember { mutableStateOf(Apartment("", "", "", "", "", "")) }
        var emptyUploadsListState = remember { mutableStateListOf<Apartment>() }

        var apartments = apartmentRepository.viewApartments(emptyUploadState, emptyUploadsListState)

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "All Apartments",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Normal,
                color = Color.Black)

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn {
                items(apartments) { apartment ->
                    ApartmentItem(
                        imageUrl = apartment.imageUrl,
                        name = apartment.apartmentName,
                        location = apartment.location,
                        price = apartment.price,
                        description = apartment.description,
                        id = apartment.id,
                        navController = navController,
                        apartmentRepository = apartmentRepository
                    )
                }
            }
        }
    }
}

@Composable
fun ApartmentItem(
    imageUrl: String,
    name: String,
    location: String,
    price: String,
    description: String,
    id: String,
    navController: NavHostController,
    apartmentRepository: ApartmentViewModel
) {
    var showFullText by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .padding(10.dp)
                .height(210.dp)
                .animateContentSize(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = Color.Gray)
        ) {
            Row {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        modifier = Modifier
                            .width(200.dp)
                            .height(150.dp)
                            .padding(10.dp),
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    Row {
                        Button(
                            onClick = {
                                apartmentRepository.deleteApartment(context.toString(), id, navController)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(Color.Red)
                        ) {
                            Text(text = "DELETE", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Button(onClick = {
                            navController.navigate("$ROUTE_EDIT/$id")
                        }, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(Color.Green)) {
                            Text(text = "UPDATE", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }

                Column(modifier = Modifier.padding(vertical = 20.dp, horizontal = 15.dp)) {
                    Text(text = "NAME", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = name, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    Text(text = "LOCATION", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = location, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    Text(text = "PRICE", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = price, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)

                    Text(text = "DESCRIPTION", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(
                        modifier = Modifier.clickable { showFullText = !showFullText },
                        text = description,
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = if (showFullText) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ViewApartmentsPreview() {
    ViewApartmentsScreen(rememberNavController())
}
