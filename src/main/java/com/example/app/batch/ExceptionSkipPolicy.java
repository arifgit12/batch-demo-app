package com.example.app.batch;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

public class ExceptionSkipPolicy implements SkipPolicy {

    @Override
    public boolean shouldSkip(Throwable throwable, long skipCount) throws SkipLimitExceededException {

        if (throwable instanceof NullPointerException)
            return throwable instanceof NullPointerException;
        else if(throwable instanceof NumberFormatException)
            return throwable instanceof NumberFormatException;

        return throwable instanceof Exception;
    }
}
