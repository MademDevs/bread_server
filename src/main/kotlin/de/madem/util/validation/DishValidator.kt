package de.madem.util.validation

import de.madem.model.api.Additive
import de.madem.model.api.NullableAdditive
import de.madem.model.api.NullableDish
import de.madem.model.api.NullableFoodTag

class DishValidator : Validator<NullableDish?> {

    //#region Fields
    private val additiveValidator = AdditiveValidator()
    private val foodTagValidator = FoodTagValidator()
    private val urlValidator = WebUrlValidator()
    //#endregion

    override fun isValid(param: NullableDish?): Boolean = with(param){
        return@with this != null
                && !(this.title.isNullOrBlank())
                && price != null
                && price >= 0
                && isValidAdditives(additives)
                && isValidFoodTags(foodTags)
                && (this.pictureURL == null || urlValidator.isValid(pictureURL))
    }

    //#region Help functions
    private fun isValidAdditives(additives: List<NullableAdditive>?) : Boolean{
        return additives?.all { additiveValidator.isValid(it) } ?: false
    }

    private fun isValidFoodTags(foodTags: List<NullableFoodTag>?): Boolean{
        return foodTags?.all { foodTagValidator.isValid(it) } ?: false
    }
    //#endregion

}