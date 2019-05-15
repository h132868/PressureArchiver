package com.Sematek.PressureArchiver;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.Sematek.PressureArchiver.LoginUtil.*;

class Archiver {
    private final MongoClient mongoClient;
        private MongoDatabase db;
        private MongoCollection<Document> coll;

        Archiver() {
            mongoClient = new MongoClient(new MongoClientURI(getMongoConnURI()));
            db = mongoClient.getDatabase(getDbName());
        }
        void archiveData(String topic, String jsonString) {
            Map m;
            try {
                m = new Gson().fromJson(jsonString, Map.class);
            }
            catch (JsonParseException e) {
                System.out.println("Message \"" + jsonString + " is not a valid JSON string, discarding");
                m = null;
            }
            if (m != null) {
                Document doc = Document.parse(jsonString);
                //Re-insert the reading as a number
                Double tmpVal = Double.parseDouble(doc.get("val").toString());
                doc.remove("val");
                doc.append("val", tmpVal);
                doc.append("topic", topic);
                //Re-insert the time as time
                doc.append("isotime", LocalDateTime.parse(doc.get("time").toString(), DateTimeFormatter.ISO_DATE_TIME));
                String dbPattern = "^\\w*(?=/)";                                     // regex for db: ^\w*(?=/)
                db = mongoClient.getDatabase(getNameFromTopic(dbPattern, topic));    //regex for coll: (?<=^\w*/)\w*
                String collPattern = "(?<=^\\w*/)\\w*";
                coll = db.getCollection(getNameFromTopic(collPattern, topic));
                System.out.println("Archiver->Saving: " + topic + " val:" + tmpVal + " time:" + LocalDateTime.parse(doc.get("time").toString(), DateTimeFormatter.ISO_DATE_TIME));
                coll.insertOne(doc);
            }
        }

    private String getNameFromTopic(String pattern, String topic) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(topic);
        if (m.find()) {
            return m.group();
        } else {
            return null;
        }
    }
}
