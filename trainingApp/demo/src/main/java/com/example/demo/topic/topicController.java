package com.example.demo.topic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
public class topicController {
    @Autowired
    private TopicService topicService; //Marks that it needs dependancy injection

    @GetMapping("/topics")
    public List<Topic> getAlltopics() {
        return topicService.getAlltopics();
    }

//    @GetMapping("/topics/spring/{id}")
//    public Topic getTopic(@PathVariable String id){
//        return topicService.getTopic(id);
//    }
    @GetMapping("/topics/spring/{foo}")
    public Topic getTopic(@PathVariable("foo") String id){
        return topicService.getTopic(id);
    }

    @PostMapping("/topics/postme")
    public void postTopic(@RequestBody Topic topic){
        topicService.addTopic(topic);
    }

    @PutMapping("/topics/putme/{id}")
    public void updateTopic(@RequestBody Topic topic, @PathVariable String id){
        topicService.updateTopic(id,topic);
    }

    @DeleteMapping("/deleteme/{id}")
    public void deleteMe(@PathVariable String id){
        topicService.removeThis(id);
    }
}
