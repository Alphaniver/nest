package com.example.nest.models

class Apartment {
    var imageUrl: String=""
    var apartmentName : String=""
    var location : String =""
    var price:String=""
    var description: String=""
    var id :String=""

    constructor(imageUrl: String, apartmentName: String, location:String,price:String,description:String,id:String){

        this.imageUrl=imageUrl
        this.apartmentName=apartmentName
        this.location=location
        this.price=price
        this.description=description
        this.id=id


    }

    constructor(
    )
}