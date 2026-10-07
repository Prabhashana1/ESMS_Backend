package lk.megasupply.esms_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// මේ Annotation එකෙන් කියන්නේ මේ Error එක ආවොත් Frontend එකට 404 Status එක යවන්න කියලයි.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}