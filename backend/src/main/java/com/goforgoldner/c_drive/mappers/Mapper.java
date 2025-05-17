package com.goforgoldner.c_drive.mappers;

public interface Mapper<A, B> {

  B mapTo(A a);

  A mapFrom(B b);
}
