package com.roy.examples;

import com.roy.examples.exceptions.FatalException;
import com.roy.examples.exceptions.TryAfterSometimeException;
import com.roy.examples.exceptions.TryImmediatelyAgainException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExternalService {


    private List<RuntimeException> exceptions = new ArrayList<>();

    public ExternalService() {
        this.exceptions.addAll(Arrays.asList(new FatalException()
                , new TryAfterSometimeException(), new TryImmediatelyAgainException()));
    }

    public void execute(String data) {
        System.out.println("Executing on " + data + " at " + new Date());
        if(data.contains("fatal")){
            throw new FatalException();
        }
        if(data.contains("blocking")){
            throw new TryImmediatelyAgainException();
        }
        if(data.contains("non")){
            throw new TryAfterSometimeException();
        }
    }
}
