package de.madem.util.validation

import de.madem.model.api.NullableFoodTag

class FoodTagValidator : Validator<NullableFoodTag?> {
    override fun isValid(param: NullableFoodTag?): Boolean = with(param) {
        return@with this != null
                && !(this.title.isNullOrBlank())
    }
}