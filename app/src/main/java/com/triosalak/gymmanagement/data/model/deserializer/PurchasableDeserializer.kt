package com.triosalak.gymmanagement.data.model.deserializer

import com.google.gson.*
import com.triosalak.gymmanagement.data.model.entity.Purchasable
import com.triosalak.gymmanagement.data.model.entity.PurchasableMembershipPackage
import com.triosalak.gymmanagement.data.model.entity.PurchasableGymClass
import java.lang.reflect.Type

class PurchasableDeserializer : JsonDeserializer<Purchasable> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Purchasable? {
        if (json == null || !json.isJsonObject) {
            return null
        }

        val jsonObject = json.asJsonObject

        // Check if it has duration field (MembershipPackage specific)
        return if (jsonObject.has("duration")) {
            // It's a MembershipPackage
            context?.deserialize(json, PurchasableMembershipPackage::class.java)
        } else {
            // It's a GymClass (doesn't have duration field)
            context?.deserialize(json, PurchasableGymClass::class.java)
        }
    }
}