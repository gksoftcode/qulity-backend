package com.wisecode.core.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PairData <T,E>{
    T key;
    E value;
}
