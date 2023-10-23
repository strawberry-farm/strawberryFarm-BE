package com.strawberryfarm.fitingle.scheduler;

import com.strawberryfarm.fitingle.domain.field.service.FieldService;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FieldScheduler {

    private final FieldService fieldService;

    //처음 어플리케이션 시작시 (s3 -> DB)실행
    @PostConstruct
    public void initFields() {
        fieldService.saveFieldsToDatabase();
    }
}
