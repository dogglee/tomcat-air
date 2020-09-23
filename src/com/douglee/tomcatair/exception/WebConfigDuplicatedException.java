package com.douglee.tomcatair.exception;

public class WebConfigDuplicatedException extends RuntimeException{
	public WebConfigDuplicatedException(String msg) {
        super(msg);
    }
}
