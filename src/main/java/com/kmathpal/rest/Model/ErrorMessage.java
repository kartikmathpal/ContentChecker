package com.kmathpal.rest.Model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorMessage {
    private String errorMessage;
    private int errorCode;
    private String errorDocumentation;

    public ErrorMessage() {
    }

    public ErrorMessage(String errorMessage, int errorCode, String errorDocumentation) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.errorDocumentation = errorDocumentation;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDocumentation() {
        return errorDocumentation;
    }

    public void setErrorDocumentation(String errorDocumentation) {
        this.errorDocumentation = errorDocumentation;
    }
}
