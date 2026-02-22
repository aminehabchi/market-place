package com.buy01.users.Exceptions;

public class UserExistException extends RuntimeException {
    public UserExistException(String msg) {
        super(msg);
    }
}
