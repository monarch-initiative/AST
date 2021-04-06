package org.monarchinitiative.ast.exception;

public class AstException extends Exception {


    public AstException() { super(); }
    public AstException(String message) { super(message);}
    public AstException(String message, Exception e) { super(message,e);}


}
