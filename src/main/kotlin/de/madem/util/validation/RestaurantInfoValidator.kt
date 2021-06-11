package de.madem.util.validation

import de.madem.model.api.NullableDish
import de.madem.model.api.NullableRestaurantInfo

class RestaurantInfoValidator : Validator<NullableRestaurantInfo?> {

    //#region fields
    private val restaurantValidator  = RestaurantValidator()
    private val addressValidator = AddressValidator()
    private val dishValidator = DishValidator()
    //#endregion

    //#region Interface functions
    override fun isValid(param: NullableRestaurantInfo?): Boolean = with(param) {
        return this != null
                && restaurantValidator.isValid(restaurant)
                && addressValidator.isValid(address)
                && isValidDishes(dishes)

    }
    //#endregion

    //#region help functions
    private fun isValidDishes(dishes: List<NullableDish>?) : Boolean = with(dishes){
        return@with this != null
                && all { dishValidator.isValid(it) }
    }
    //#endregion

}