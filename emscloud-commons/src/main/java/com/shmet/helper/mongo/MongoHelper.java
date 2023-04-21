package com.shmet.helper.mongo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import com.mongodb.BasicDBObject;

public class MongoHelper {

    public static int bathUpdate(MongoTemplate mongoTemplate, Class<?> entityClass,
                                 List<MongoBathUpdateOptions> options) {
        String collectionName = determineCollectionName(entityClass);
        return doBathUpdate(mongoTemplate, collectionName, options, true);
    }

    private static int doBathUpdate(MongoTemplate mongoTemplate, String collName,
                                    List<MongoBathUpdateOptions> options, boolean ordered) {
        org.bson.Document command = new org.bson.Document();
        command.put("update", collName);
        List<BasicDBObject> updateList = new ArrayList<>();
        for (MongoBathUpdateOptions option : options) {
            BasicDBObject update = new BasicDBObject();
            update.put("q", option.getQuery().getQueryObject());
            update.put("u", option.getUpdate().getUpdateObject());
            update.put("upsert", option.isUpsert());
            update.put("multi", option.isMulti());
            updateList.add(update);
        }
        command.put("updates", updateList);
        command.put("ordered", ordered);
        org.bson.Document commandResult = mongoTemplate.executeCommand(command);
        return Integer.parseInt(commandResult.get("n").toString());
    }

    private static String determineCollectionName(Class<?> entityClass) {
        if (entityClass == null) {
            throw new InvalidDataAccessApiUsageException(
                    "No class parameter provided, entity collection can't be determined!");
        }
        String collName = entityClass.getSimpleName();
        if (entityClass.isAnnotationPresent(Document.class)) {
            Document document = entityClass.getAnnotation(Document.class);
            collName = document.collection();
        } else {
            collName =
                    collName.replaceFirst(collName.substring(0, 1), collName.substring(0, 1).toLowerCase());
        }
        return collName;
    }
}
