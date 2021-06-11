package de.madem.util.validation

import de.madem.model.api.NullableRestaurant

class RestaurantValidator : Validator<NullableRestaurant?> {

    //#region Fields
    private val urlValidator = WebUrlValidator()
    private val phoneValidator = PhoneValidator()
    //#endregion

    override fun isValid(param: NullableRestaurant?): Boolean = with(param) {
        return@with this != null
                && !(this.title.isNullOrBlank())
                && phoneValidator.isValid(phone)
                && (this.pictureURL == null || urlValidator.isValid(pictureURL))
    }
}