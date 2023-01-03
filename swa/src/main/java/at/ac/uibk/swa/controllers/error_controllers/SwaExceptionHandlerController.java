package at.ac.uibk.swa.controllers.error_controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

/**
 * Controller Advice for catching Exceptions and sending the appropriate Responses.
 *
 * @author David Rieser
 */
@ControllerAdvice
@SuppressWarnings("unused")
public class SwaExceptionHandlerController extends ResponseEntityExceptionHandler {

    @Autowired
    private SwaErrorController errorController;

    // https://developer.mozilla.org/en-US/docs/Web/HTTP/Status#client_error_responses
    @ExceptionHandler(AuthenticationException.class)
    public void handleAuthenticationException(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        errorController.handleErrorManual(request, response, authException);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        errorController.handleAuthorizationError(request, response, accessDeniedException);
    }

    @ExceptionHandler(Exception.class)
    public void handleException(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception
    ) throws IOException {
        errorController.handleErrorManual(request, response, exception);
    }
}
