package com.example.poll.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author huhao
 * @created 2022/10/10
 * Description ResourceNotFoundException
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    private String resourceName;
    private String filedName;
    private Object filedValue;

    public ResourceNotFoundException(String resourceName, String filedName, Object filedValue) {
        super(String.format("%s not found with % : '%s'", resourceName, filedName, filedValue));
        this.resourceName = resourceName;
        this.filedName = filedName;
        this.filedValue = filedValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFiledName() {
        return filedName;
    }

    public Object getFiledValue() {
        return filedValue;
    }
}
