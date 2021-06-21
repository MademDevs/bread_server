package de.madem.routing

import de.madem.util.security.JwtConfig
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authentication
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext

suspend fun PipelineContext<Unit, ApplicationCall>.authenticateJwtUserWithUrlId(urlUserId: Int) : Boolean{
    val authUserId = (call.authentication.principal as? JwtConfig.JwtUser)?.userId
    if(authUserId == null){
        call.respond(HttpStatusCode.Unauthorized)
        return false
    }
    else if (authUserId != urlUserId){
        call.respond(HttpStatusCode.Forbidden)
        return false
    }

    return true
}