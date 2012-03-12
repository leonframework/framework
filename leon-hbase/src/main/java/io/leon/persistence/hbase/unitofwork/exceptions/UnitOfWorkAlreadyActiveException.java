package io.leon.persistence.hbase.unitofwork.exceptions;

public class UnitOfWorkAlreadyActiveException extends IllegalStateException {

    public UnitOfWorkAlreadyActiveException() {
        super("The unit of work was already started for this thread.");
    }

}
