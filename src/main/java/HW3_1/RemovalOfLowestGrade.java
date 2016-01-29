package HW3_1;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Objects;

/**
 * Created by iuliab on 22.01.2016.
 */
public class RemovalOfLowestGrade {

    public static void main(String[] args) {
        MongoClient client = new MongoClient();
        MongoDatabase db = client.getDatabase("school");
        MongoCollection<Document> students = db.getCollection("students");

        //1 means include "scores" in the projection; 0 means exclude
        //if we want to exclude/include many, append
        MongoCursor<Document> cursor = students.find().iterator();
        //de ce nu merge?? - cu scores.$.type
        //System.out.println(new Document("scores.$.type", "homework"));
        //List<Document> docs = students.find(new Document("scores.type", "homework")).projection(new Document("scores.score", 1).append("_id", 0)).into(new ArrayList<Document>());

        try {
            while (cursor.hasNext()) {
                Document student = cursor.next();
                List<Document> scores = (List<Document>) student.get("scores");
                Document minScoreObj = null;
                double minScore = Double.MAX_VALUE;

                for (Document scoreDocument : scores) {
                    double score = scoreDocument.getDouble("score");
                    String type = scoreDocument.getString("type");

                    if (type.equals("homework") && score < minScore) {
                        minScore = score;
                        minScoreObj = scoreDocument;
                    }
                }

                if (minScoreObj != null) {
                    scores.remove(minScoreObj);
                }

                students.updateOne(Filters.eq("_id", student.get("_id")), new Document("$set", new Document("scores", scores)));
            }
        } finally {
            cursor.close();
        }
        client.close();
    }
}
