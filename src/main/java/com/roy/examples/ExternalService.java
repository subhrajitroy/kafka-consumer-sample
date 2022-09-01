package com.roy.examples;

import com.roy.examples.exceptions.FatalException;
import com.roy.examples.exceptions.TryAfterSometimeException;
import com.roy.examples.exceptions.TryImmediatelyAgainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ExternalService {

    Logger logger = LoggerFactory.getLogger(ExternalService.class);

    public void execute(String data) {
        final String info = "Executing on " + data + " at " + new Date();
        System.out.println(info);
//        logger.info(info);
        if(data.contains("never")){
            throw new FatalException();
        }
        if(data.contains("now")){
            throw new TryImmediatelyAgainException();
        }
        if(data.contains("later")){
            throw new TryAfterSometimeException();
        }
    }
}
