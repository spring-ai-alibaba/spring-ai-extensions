package com.alibaba.cloud.ai.mcp.register.utils;

public class CheckCompatibleResult {
    
    private final boolean compatible;
    
    private String message;
    
    public CheckCompatibleResult(boolean compatible, String message) {
        this.compatible = compatible;
        this.message = message;
    }
    
    public CheckCompatibleResult(boolean compatible) {
        this.compatible = compatible;
    }
    
    public boolean isCompatible() {
        return compatible;
    }
    
    public String getMessage() {
        return message;
    }
    
}
