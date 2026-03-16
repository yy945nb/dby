package com.dby.common.exception;

/**
 * 资源未找到异常
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, Object id) {
        super(404, resource + " not found with id: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(404, message);
    }
}
