package com.example.nest.ui.theme.screens.landlord



import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.nest.navigation.ROUTE_ADD_APARTMENT
import com.example.nest.navigation.ROUTE_VIEW_APARTMENTS
import java.util.UUID


@Composable
fun LandlordPost(navController: NavController) {
    val imageUris = rememberSaveable { mutableStateListOf<Uri>() }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        imageUris.addAll(uris)
    }

    var apartmentName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(10.dp).fillMaxWidth()) {

        Text(
            text = "APARTMENT INFORMATION",
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(20.dp).background(Color.White)
        )

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Attach Pictures")
        }

        LazyRow(modifier = Modifier.padding(10.dp)) {
            items(imageUris) { uri ->
                Image(
                    painter = rememberAsyncImagePainter(model = uri),
                    contentDescription = null,
                    modifier = Modifier.padding(5.dp).size(100.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        OutlinedTextField(
            value = apartmentName,
            onValueChange = {
                apartmentName = it
                Log.d("LandlordPost", "Apartment Name: $apartmentName")
            },
            label = { Text("Enter Apartment Name") },
            modifier = Modifier.fillMaxWidth().padding(10.dp).focusable()
        )
        Spacer(modifier = Modifier.height(5.dp))

        OutlinedTextField(
            value = location,
            onValueChange = {
                location = it
                Log.d("LandlordPost", "Location: $location")
            },
            label = { Text("Enter Location") },
            modifier = Modifier.fillMaxWidth().padding(10.dp).focusable()
        )
        Spacer(modifier = Modifier.height(5.dp))

        OutlinedTextField(
            value = price,
            onValueChange = {
                price = it
                Log.d("LandlordPost", "Price: $price")
            },
            label = { Text("Enter Price") },
            modifier = Modifier.fillMaxWidth().padding(10.dp).focusable()
        )
        Spacer(modifier = Modifier.height(5.dp))

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                Log.d("LandlordPost", "Description: $description")
            },
            label = { Text("Enter Apartment Description") },
            modifier = Modifier.fillMaxWidth().padding(10.dp).height(150.dp).focusable(),
            singleLine = false
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { navController.navigate(ROUTE_VIEW_APARTMENTS) }) {
                Text(text = "VIEW ALL APARTMENTS")
            }
            Button(onClick = {
                if (imageUris.isNotEmpty()) {
                    val apartmentId = UUID.randomUUID().toString()
                    val apartment = "$apartmentId.jpg"

                    navController.navigate(ROUTE_ADD_APARTMENT)
                } else {
                    Toast.makeText(context, "Please select at least one image", Toast.LENGTH_LONG).show()
                }
            }) {
                Text(text = "SAVE")
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LandlordPostPreview() {
    LandlordPost(rememberNavController())
}
