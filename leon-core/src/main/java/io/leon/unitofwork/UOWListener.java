package io.leon.unitofwork;

public interface UOWListener {

    void begin();

    void commit();

}
