package com.rentacar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final Long resourceId;
    private final String resourceType;
    
    public ResourceNotFoundException(String resourceType, Long id) {
        super(resourceType + " not found with id: " + id);
        this.resourceId = id;
        this.resourceType = resourceType;
    }
    
    public Long getResourceId() {
        return resourceId;
    }
    
    public String getResourceType() {
        return resourceType;
    }
}
