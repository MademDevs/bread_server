package de.madem.util.validation

import de.madem.model.api.NullableAddress

class AddressValidator : Validator<NullableAddress?> {

    override fun isValid(param: NullableAddress?): Boolean = with(param) {
        return@with this != null
                && !(street.isNullOrBlank())
                && !(number.isNullOrBlank())
                && isValidZipCode(zipCode)
                && !(city.isNullOrBlank())
                && !(country.isNullOrBlank())
    }

    //#region Help functions
    private fun isValidZipCode(zipCode : String?) : Boolean{
        return zipCode != null
                && zipCode.matches(Regex("^[0-9]{5}\$"))
    }
    //#endregion
}