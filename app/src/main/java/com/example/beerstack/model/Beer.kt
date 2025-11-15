package com.example.beerstack.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

//all the values for the api (punk)
@Serializable
data class Beer(
    val id: Int,
    val name: String,
    val price: String?,
    val image: String? = null, //sometimes no given input, standard is null value
    val rating: Rating? = null
)

//The rating is stored in json with 2 diffrent values,
// the average review /5 and how many reviews it has
//Serialization = save temporary in memory
@Serializable(with = RatingOrStringSerializer::class)
data class Rating(
    val average: Double = 0.0,
    val reviews: Int = 0
)

// Custom serializer to handle rating as object or string
// handle the 'rating' field when it's either a JSON object (with average and reviews), or just a string like "".
object RatingOrStringSerializer : KSerializer<Rating?> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Rating")

    //Convert JSON data into Rating objects
    override fun deserialize(decoder: Decoder): Rating? {
        // Make sure we are working with JSON
        val input = decoder as? JsonDecoder
            ?: throw IllegalStateException("Only works with Json format.")
        // get the JSON element for the 'rating' field
        val element = input.decodeJsonElement()
        // If the element is a JSON object, extract fields
        return when (element) {
            is JsonObject -> {
                val avg = element["average"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                val rev = element["reviews"]?.jsonPrimitive?.intOrNull ?: 0
                Rating(avg, rev) // Build Rating instance
            }
            is JsonPrimitive -> null // If it's just a string (like "") -> missing
            else -> null //Anything else is ignored (shouldnt be the case)
        }
    }
    // Deserialize: fetch, split up and READ!
    // Serialize: convert object to JSON, wich we dont need in this case
    override fun serialize(encoder: Encoder, value: Rating?) {
        //So just to tell that we dont do anything anymore with the object (to JSON)
        throw NotImplementedError()
    }
}