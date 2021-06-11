package de.madem.util.validation

import de.madem.model.api.NullableAccountLoginRequest

class AccountLoginRequestValidator : Validator<NullableAccountLoginRequest?> {
    override fun isValid(param: NullableAccountLoginRequest?): Boolean = with(param){
        return this != null && !(password.isNullOrBlank()) && !(username.isNullOrBlank())
    }
}