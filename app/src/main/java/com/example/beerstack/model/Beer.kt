package com.example.beerstack.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

//all the values for the api (punk)
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
object RatingOrStringSerializer : KSerializer<Rating?> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Rating")

    override fun deserialize(decoder: Decoder): Rating? {
        val input = decoder as? JsonDecoder
            ?: throw IllegalStateException("Only works with Json format.")
        val element = input.decodeJsonElement()
        return when (element) {
            is JsonObject -> {
                val avg = element["average"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                val rev = element["reviews"]?.jsonPrimitive?.intOrNull ?: 0
                Rating(avg, rev)
            }
            is JsonPrimitive -> null // Covers "" or other primitive types
            else -> null
        }
    }
    override fun serialize(encoder: Encoder, value: Rating?) {
        throw NotImplementedError()
    }
}