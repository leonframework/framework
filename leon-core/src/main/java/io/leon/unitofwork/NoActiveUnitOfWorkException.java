package io.leon.unitofwork;

public class NoActiveUnitOfWorkException extends IllegalStateException {

    public NoActiveUnitOfWorkException() {
        super("No active unit of work.");
    }

}
