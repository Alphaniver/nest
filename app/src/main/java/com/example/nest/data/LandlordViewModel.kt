package com.example.nest.data



import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.nest.models.Apartment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

    class LandlordViewModel(var context: Context) {

//        var authRepository: AuthViewModel

        init {
//            authRepository = AuthViewModel(navController, context)
        }

        fun saveLandlord(
            filePath: Uri,
            apartmentName: String,
            location: String,
            price: String,
            description: String
        ) {
            val id = System.currentTimeMillis().toString()
            val storageReference =
                FirebaseStorage.getInstance().getReference().child("LandlordPictures/$id")

            storageReference.putFile(filePath).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        val landlordData =
                            Apartment(imageUrl, apartmentName, location, price, description, id)
                        val dbRef =
                            FirebaseDatabase.getInstance().getReference().child("Landlords/$id")
                        dbRef.setValue(landlordData)
                        Toast.makeText(context, "Landlord saved successfully", Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    Toast.makeText(context, "${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        fun viewLandlords(
            landlord: MutableState<Apartment>,
            landlords: SnapshotStateList<Apartment>
        ): SnapshotStateList<Apartment> {
            val ref = FirebaseDatabase.getInstance().getReference().child("Landlords")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    landlords.clear()
                    for (snap in snapshot.children) {
                        val value = snap.getValue(Apartment::class.java)
                        landlord.value = value!!
                        landlords.add(value)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                }
            })
            return landlords
        }

        fun updateLandlord(
            filePath: Uri?,
            apartmentName: String?,
            location: String?,
            price: String?,
            description: String?,
            id: String
        ) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Landlords/$id")

            databaseReference.get().addOnSuccessListener { dataSnapshot ->
                val currentLandlord = dataSnapshot.getValue(Apartment::class.java)

                if (currentLandlord != null) {
                    val updatedLandlord = Apartment(
                        imageUrl = if (filePath != null && filePath != Uri.EMPTY) "" else currentLandlord.imageUrl,
                        apartmentName = apartmentName ?: currentLandlord.apartmentName,
                        location = location ?: currentLandlord.location,
                        price = price ?: currentLandlord.price,
                        description = description ?: currentLandlord.description,
                        id = id
                    )

                    if (filePath != null && filePath != Uri.EMPTY) {
                        val storageReference = FirebaseStorage.getInstance().reference
                        val imageRef =
                            storageReference.child("LandlordPictures/${UUID.randomUUID()}.jpg")

                        imageRef.putFile(filePath).addOnSuccessListener {
                            imageRef.downloadUrl.addOnSuccessListener { uri ->
                                updatedLandlord.imageUrl = uri.toString()
                                databaseReference.setValue(updatedLandlord)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                "Update successful",
                                                Toast.LENGTH_SHORT
                                            ).show()
//                                            navController.navigate(ROUTE_VIEW_LANDLORD)
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Update failed: ${task.exception?.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                        }.addOnFailureListener { exception ->
                            Toast.makeText(
                                context,
                                "Image upload failed: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        databaseReference.setValue(updatedLandlord).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Update successful", Toast.LENGTH_SHORT)
                                    .show()
//                                navController.navigate(ROUTE_VIEW_LANDLORD)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Update failed: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Landlord not found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to retrieve landlord data", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        fun deleteLandlord(id: String) {
            val progressDialog = ProgressDialog(context).apply {
                setMessage("Deleting landlord...")
                setCancelable(false)
                show()
            }

            val delRef = FirebaseDatabase.getInstance().getReference("Landlords/$id")

            delRef.removeValue().addOnCompleteListener { task ->
                progressDialog.dismiss()

                if (task.isSuccessful) {
                    Toast.makeText(context, "Landlord deleted successfully", Toast.LENGTH_SHORT)
                        .show()
//                    navController.navigate(ROUTE_VIEW_LANDLORD)
                } else {
                    Toast.makeText(
                        context,
                        task.exception?.message ?: "Deletion failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        fun saveApartment() {

        }
    }

