package de.madem.util.validation

class WebUrlValidator : Validator<String?> {
    override fun isValid(param: String?): Boolean {
        return !(param.isNullOrBlank())
                && param.matches(Regex("^https?:\\/\\/([^/%&#?=])+(:0|:[1-9][0-9]{0,4})?(/.+)*(/[^\\n]*)?\$"))
    }
}