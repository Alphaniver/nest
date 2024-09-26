package com.example.nest.data

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.nest.models.Apartment
import com.example.nest.navigation.ROUTE_VIEW_APARTMENTS
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ApartmentViewModel(var navController: NavController, var context: Context) {

    fun saveApartment(filePath: Uri, name: String, location: String, price: String, description: String) {
        val id = System.currentTimeMillis().toString()
        val storageReference = FirebaseStorage.getInstance().reference.child("Apartments/$id")

        storageReference.putFile(filePath).addOnCompleteListener {
            if (it.isSuccessful) {
                storageReference.downloadUrl.addOnSuccessListener { imageUrl ->
                    val apartmentData = Apartment(imageUrl.toString(), name, location, price, description, id)
                    val dbRef = FirebaseDatabase.getInstance().getReference("Apartments/$id")
                    dbRef.setValue(apartmentData)
                    Toast.makeText(context, "Apartment saved successfully", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "${it.exception!!.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun viewApartments(apartment: MutableState<Apartment>, apartments: SnapshotStateList<Apartment>): SnapshotStateList<Apartment> {
        val ref = FirebaseDatabase.getInstance().getReference("Apartments")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                apartments.clear()
                for (snap in snapshot.children) {
                    val value = snap.getValue(Apartment::class.java)
                    if (value != null) {
                        apartment.value = value
                        apartments.add(value)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            }
        })
        return apartments
    }

    fun updateApartment(
        filePath: Uri?,
        name: String?,
        location: String?,
        price: String?,
        description: String?,
        id: String,
        currentImageUrl: String
    ) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Apartments/$id")

        // Retrieve the current apartment data
        databaseReference.get().addOnSuccessListener { dataSnapshot ->
            val currentApartment = dataSnapshot.getValue(Apartment::class.java)

            if (currentApartment != null) {
                // Prepare updated apartment data
                val updatedApartment = Apartment(
                    imageUrl = if (filePath != null && filePath != Uri.EMPTY) "" else currentApartment.imageUrl,
                    apartmentName = name ?: currentApartment.apartmentName,
                    location = location ?: currentApartment.location,
                    price = price ?: currentApartment.price,
                    description = description ?: currentApartment.description,
                    id = id
                )

                // Upload new image if provided
                if (filePath != null && filePath != Uri.EMPTY) {
                    val storageReference = FirebaseStorage.getInstance().reference
                    val imageRef = storageReference.child("Apartments/${UUID.randomUUID()}.jpg")

                    imageRef.putFile(filePath)
                        .addOnSuccessListener {
                            imageRef.downloadUrl.addOnSuccessListener { uri ->
                                updatedApartment.imageUrl = uri.toString() // Update the image URL
                                databaseReference.setValue(updatedApartment)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show()
                                            navController.navigate(ROUTE_VIEW_APARTMENTS)
                                        } else {
                                            Toast.makeText(context, "Update failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(context, "Image upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // No new image, just update other fields
                    databaseReference.setValue(updatedApartment)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT).show()
                                navController.navigate(ROUTE_VIEW_APARTMENTS)
                            } else {
                                Toast.makeText(context, "Update failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } else {
                Toast.makeText(context, "Apartment not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to retrieve apartment data", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteApartment(id: String, id1: String, navController: NavHostController) {
        val progressDialog = ProgressDialog(context).apply {
            setMessage("Deleting apartment...")
            setCancelable(false)
            show()
        }

        val delRef = FirebaseDatabase.getInstance().getReference("Apartments/$id")

        delRef.removeValue().addOnCompleteListener { task ->
            progressDialog.dismiss()

            if (task.isSuccessful) {
                Toast.makeText(context, "Apartment deleted successfully", Toast.LENGTH_SHORT).show()
                this.navController.navigate(ROUTE_VIEW_APARTMENTS)
            } else {
                Toast.makeText(context, task.exception?.message ?: "Deletion failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
