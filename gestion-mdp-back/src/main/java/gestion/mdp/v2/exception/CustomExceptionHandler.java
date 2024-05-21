package gestion.mdp.v2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class CustomExceptionHandler {

    public ResponseEntity<String> handleCustomInternalServerError(MyExceptionServerIssue exceptionServerIssue){
        return new ResponseEntity<>(exceptionServerIssue.getMessage(), HttpStatus.NOT_FOUND);
    }
}
