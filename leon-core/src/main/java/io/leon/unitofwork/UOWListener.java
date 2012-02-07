package io.leon.unitofwork;

public interface UOWListener {

    void begin(Object o);

    void commit(Object o);

}
