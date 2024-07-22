package com.booker.core.wrapper;

import java.io.Serializable;

public interface ResultInterface<T> extends Serializable {
    T getResult();
}
