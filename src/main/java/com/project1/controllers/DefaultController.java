package com.project1.controllers;

import com.project1.model.Message;
import com.project1.utils.MessageType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {

    @RequestMapping
    public ResponseEntity<?> forwardRequest() {
        return new ResponseEntity<Message>(new Message(MessageType.ERROR, "Bad request"), HttpStatus.BAD_REQUEST);
    }

}
