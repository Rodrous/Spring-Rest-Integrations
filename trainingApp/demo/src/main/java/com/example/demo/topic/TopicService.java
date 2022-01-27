package com.example.demo.topic;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//To make a service, you need @Service annotation
@Service
public class TopicService {

    private List<Topic> topics = new ArrayList<>(Arrays.asList(
                new Topic("1","Hello","World!"),
                new Topic("2","Spring","framework"),
                new Topic("3","Attack on", "titians")
        ));

    public List<Topic> getAlltopics(){
        return topics;
    }
    public Topic getTopic(String id){
        return topics.stream().filter(t -> t.getId().equals(id)).findFirst().get();
    }


    public void addTopic(Topic topic) {
        topics.add(topic);
    }

    public void updateTopic(String id, Topic topic) {
        for (int i = 0; i < topics.size() ; i++) {
            Topic t = topics.get(i);
            if (t.getId().equals(id)){
                topics.set(i,topic);
                return;
            }

        }
    }

    public void removeThis(String id) {
        topics.removeIf(t-> t.getId().equals(id));
    }
}
