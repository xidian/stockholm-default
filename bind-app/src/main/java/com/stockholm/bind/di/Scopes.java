package com.stockholm.bind.di;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

public interface Scopes {

    @Documented
    @Scope
    @Retention(RetentionPolicy.RUNTIME)
    @interface Activity {
    }

}
