package HW2_3;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class RemoveLowestHWGrade {

    public static void main(final String[] args) {
        MongoClient client = new MongoClient();
        MongoDatabase db = client.getDatabase("students");
        MongoCollection<Document> grades = db.getCollection("grades");

        Document filter = new Document("type", "homework");
        MongoCursor<Document> cursor = grades.find(filter).sort(new Document("student_id", 1)).iterator();
        //sau find(eq("type", "homework")) cu static import

        Object studentId = -1;
        while(cursor.hasNext()) {
            Document entry = cursor.next();
            if(!entry.get("student_id").equals(studentId)) {
                Object id = entry.get("_id");
                grades.deleteOne(eq("_id", id));//eq("_id", id)
                System.out.println(entry);
            }
            studentId = entry.get("student_id");
        }
    }
}
