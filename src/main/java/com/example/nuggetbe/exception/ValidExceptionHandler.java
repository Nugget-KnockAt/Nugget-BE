package com.example.nuggetbe.exception;



import com.example.nuggetbe.dto.response.BaseException;
import com.example.nuggetbe.dto.response.BaseResponse;
import com.example.nuggetbe.dto.response.BaseResponseStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ValidExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public  ResponseEntity<BaseResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuffer errorMessage = new StringBuffer();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessage.append(error.getDefaultMessage()).append(", ");
        });

        // Remove the trailing comma and space
        errorMessage.setLength(errorMessage.length() - 2);

        return new ResponseEntity<>(new BaseResponse<String>(BaseResponseStatus.BAD_REQUEST, errorMessage.toString()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BaseException.class)
    protected BaseResponse<?> handleException(
            BaseException e,HttpServletRequest request){
        log.error("Exception: {} {}", request.getRequestURL(), e);
        return new  BaseResponse<>(e.getStatus());
    }
}
