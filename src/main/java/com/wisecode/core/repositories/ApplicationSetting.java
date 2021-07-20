package com.wisecode.core.repositories;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class ApplicationSetting {
    Boolean logEnable  = true;
}
