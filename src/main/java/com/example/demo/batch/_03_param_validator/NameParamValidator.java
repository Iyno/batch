package com.example.demo.batch._03_param_validator;

import com.example.demo.batch._02_params.ParamJob;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

import javax.security.auth.login.Configuration;
import java.lang.reflect.Parameter;

public class NameParamValidator implements JobParametersValidator {

    @Override
    public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
        String name = jobParameters.getString("name");

        if (!StringUtils.hasText(name)){
            throw new JobParametersInvalidException("name 参数值不能为null 或者 空串");

        }
    }
}
