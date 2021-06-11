package de.madem.util.validation

class PhoneValidator : Validator<String?> {
    override fun isValid(param: String?): Boolean = with(param){
        return@with this != null && matches(Regex("^\\+?[0-9]+([- \\/]?[0-9]+)*\$"))
    }
}