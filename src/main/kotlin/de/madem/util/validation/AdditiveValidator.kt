package de.madem.util.validation

import de.madem.model.api.NullableAdditive
import de.madem.model.api.NullableAddress
import de.madem.model.api.NullableFoodTag

class AdditiveValidator : Validator<NullableAdditive?>{
    override fun isValid(param: NullableAdditive?): Boolean = with(param) {
        return@with this != null
                && !(this.title.isNullOrBlank())
    }
}